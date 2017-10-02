package eu.rsulkowski.jdoocsoup.processor.handler;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import eu.rsulkowski.jdoocsoup.annotation.DataClass;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;


/**
 * Created by sulkowsk on 29/09/2017.
 */
public abstract class BaseAnnotationHandler<T extends Annotation> {

    private final static String BUILDER_NAME_POSTFIX = "Builder";

    protected final AbstractProcessor processor;
    protected final ProcessingEnvironment env;
    protected final Class<T> handledAnnotation;
    protected final ElementKind[] handledKinds;
    ;

    public BaseAnnotationHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment, Class<T> handledAnnotation, ElementKind... handledKinds) {
        this.processor = processor;
        this.env = processingEnvironment;
        this.handledAnnotation = handledAnnotation;
        this.handledKinds = handledKinds;
    }

    public abstract void process(RoundEnvironment roundEnvironment, AnnotationHandlerCallback callback);

    protected void processForApplicable(RoundEnvironment roundEnvironment, AnnotationAction action) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(handledAnnotation)) {
            if (checkIfSupportedKind(annotatedElement)) {
                action.execute(annotatedElement);
            }
        }
    }

    private boolean checkIfSupportedKind(Element annotatedElement) {
        for (ElementKind kind : handledKinds) {
            if (kind == annotatedElement.getKind()) {
                return true;
            }
        }
        return false;
    }

    protected String parseDataClassName(Element annotatedElement) {
        String dataClassName = annotatedElement.getSimpleName().toString();
        DataClass dataClass = annotatedElement.getAnnotation(DataClass.class);
        if (dataClass != null) {
            dataClassName = dataClass.name().isEmpty() ? dataClassName.replaceFirst("I", "") : dataClass.name();
        }
        return dataClassName;
    }

    protected String parseDataClassBuilderName(Element annotatedElement) {
        return parseDataClassName(annotatedElement) + BUILDER_NAME_POSTFIX;
    }

    protected Iterable<FieldSpec> createFieldsSpec(TypeElement element) {
        List<FieldSpec> fields = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            ParameterSpec parameterSpec = obtainParamSpec(el, element.getKind());
            fields.add(FieldSpec.builder(parameterSpec.type, parameterSpec.name).addModifiers(Modifier.PRIVATE)
                    .build());
        }

        return fields;
    }

    protected Iterable<ParameterSpec> createParamSpecs(TypeElement element) {
        List<ParameterSpec> params = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            if (el.getKind() == ElementKind.METHOD) {
                params.add(obtainParamSpec(el, element.getKind()));
            }
        }

        return params;
    }

    protected ParameterSpec obtainParamSpec(Element element, ElementKind superKind) {
        return obtainParamSpec(element, null, superKind);
    }

    protected ParameterSpec obtainParamSpec(Element element, String overrideName, ElementKind superKind) {

        TypeName type = null;

        env.getMessager().printMessage(Diagnostic.Kind.ERROR, element.asType().toString());

        if (superKind == ElementKind.INTERFACE && element.getKind() == ElementKind.METHOD) {
            ExecutableElement executableElement = (ExecutableElement) element;
            type = TypeName.get(executableElement.getReturnType());
        } else if (superKind == ElementKind.CLASS && element.getKind() == ElementKind.FIELD){
            type = TypeName.get(element.asType());
        } else {
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported type");
        }

        String name = overrideName == null ? element.getSimpleName().toString() : overrideName;

        return ParameterSpec.builder(type, name)
                .build();
    }

    public interface AnnotationAction {
        void execute(Element element);
    }

    public interface AnnotationHandlerCallback {
        void onTypeSpecsCreated(Pair<String, TypeSpec> packageAndTypeSpec);
    }
}
