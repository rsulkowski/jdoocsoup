package eu.rsulkowski.jdoocsoup.processor.descriptor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import lombok.Getter;

/**
 * Created by rsulkowski on 10/2/17.
 */

/**
 * Contains the description of the DataClassBuilder such as: package name, element kind etc.
 */
@Getter
public class DataClassBuilderDescriptor {

    private final static String BUILDER_NAME_POSTFIX = "Builder";

    private final DataClassBuilder annotation;
    private final String dataClassBuilderName;
    private final String packageName;
    private final String setterPrefix;
    private final ProcessingEnvironment processingEnvironment;
    private final TypeElement typeElement;
    private final ElementKind elementKind;
    private final TypeSpec.Builder typeSpecBuilder;
    private final List<ExecutableElement> methods = new ArrayList<>();
    private final List<VariableElement> fields = new ArrayList<>();
    private TypeElement buildMethodReturnType;

    public DataClassBuilderDescriptor(ProcessingEnvironment env, TypeElement element) {
        annotation = element.getAnnotation(DataClassBuilder.class);
        try {
            annotation.buildMethodReturnType();
        } catch (MirroredTypeException mte) {
            buildMethodReturnType = (TypeElement) env.getTypeUtils().asElement(mte.getTypeMirror());
        }

        this.dataClassBuilderName = annotation.name().isEmpty() ? element.getSimpleName() + BUILDER_NAME_POSTFIX : annotation.name();
        this.setterPrefix = annotation.setterPrefix();
        this.packageName = ElementsUtils.parsePackageName(env, element);
        this.processingEnvironment = env;
        this.typeElement = element;
        this.elementKind = element.getKind();
        this.typeSpecBuilder = TypeSpec.classBuilder(dataClassBuilderName);
        parseImplementInterfaces();
        typeSpecBuilder.addModifiers(Modifier.PUBLIC);
        parseAll();
    }

    private void parseImplementInterfaces() {
        AnnotationMirror annotationMirror = getAnnotationMirror(this.typeElement, DataClassBuilder.class.getName());
        AnnotationValue inflationArgsValue = getAnnotationValue(annotationMirror, "implementInterfaces");
        if (inflationArgsValue != null) {
            List<Attribute.Class> list = (List<Attribute.Class>) inflationArgsValue.getValue();
            for (Attribute.Class superInterface : list)
            {
                TypeName superI = ClassName.bestGuess(superInterface.getValue().toString());
                typeSpecBuilder.addSuperinterface(superI);
            }
        }
    }

    private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, String className) {

        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {

            if (m.getAnnotationType().toString().equals(className)) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {

                return entry.getValue();
            }
        }
        return null;
    }


    private TypeElement asTypeElement(TypeMirror typeMirror) {
        Types TypeUtils = this.processingEnvironment.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }

    private void parseAll() {
        typeSpecBuilder.addJavadoc(annotation.jdocs() + "\n");

        checkElementForMembers(typeElement);

        if (typeElement.getSuperclass() == null) {
            return;
        }

        Element superClassElement = processingEnvironment.getTypeUtils().asElement(typeElement.getSuperclass());
        checkElementForMembers(superClassElement);
    }

    private void checkElementForMembers(Element rootElement) {
        for (Element element : rootElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                methods.add((ExecutableElement) element);
            } else if (element.getKind() == ElementKind.FIELD) {
                fields.add((VariableElement) element);
            }
        }
    }
}
