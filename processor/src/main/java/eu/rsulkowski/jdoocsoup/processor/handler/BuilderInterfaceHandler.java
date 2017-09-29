package eu.rsulkowski.jdoocsoup.processor.handler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import eu.rsulkowski.jdoocsoup.annotation.BuilderInterface;
import eu.rsulkowski.jdoocsoup.annotation.Setter;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;

/**
 * Created by sulkowsk on 29/09/2017.
 */

public class BuilderInterfaceHandler extends BaseAnnotationHandler<BuilderInterface> {

    private final static String BUILDER_NAME_POSTFIX = "Builder";


    public BuilderInterfaceHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment) {
        super(processor, processingEnvironment, BuilderInterface.class, ElementKind.INTERFACE);
    }

    public void process(RoundEnvironment roundEnvironment, final AnnotationHandlerCallback callback) {
        processForApplicable(roundEnvironment, new BaseAnnotationHandler.AnnotationAction() {
            public void execute(Element annotatedElement) {
                TypeElement element = (TypeElement) annotatedElement;

                TypeSpec builderClassSpec = prepareDataClassBuilderSpec(annotatedElement, element);
                callback.onTypeSpecsCreated(new Pair(ElementsUtils.parsePackageName(env, element), builderClassSpec));

                TypeSpec dataClassSpec = prepareDataClassSpec(annotatedElement, element);
                callback.onTypeSpecsCreated(new Pair(ElementsUtils.parsePackageName(env, element), dataClassSpec));
            }
        });
    }

    private TypeSpec prepareDataClassBuilderSpec(Element annotatedElement, TypeElement element) {
        MethodSpec builder = getBuilderMethodSpec(element);
        MethodSpec build = getBuildMethodSpec(element);

        return TypeSpec.classBuilder(parseDataClassName(annotatedElement) + BUILDER_NAME_POSTFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(builder)
                .addMethod(build)
                .addMethods(createMutatorMethodsSpec(element))
                .addFields(createFieldsSpec(element))
                .build();
    }

    private TypeSpec prepareDataClassSpec(Element annotatedElement, TypeElement element) {

        String dataClassName = parseDataClassName(annotatedElement);

        return TypeSpec.classBuilder(dataClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(createDataClassConstructor(annotatedElement))
                .addFields(createFieldsSpec(element))
                .build();
    }

    private MethodSpec createDataClassConstructor(Element annotatedElement) {
        TypeElement element = (TypeElement) annotatedElement;
        Iterable<ParameterSpec> parameterSpec = createParamSpecs(element);


        MethodSpec.Builder constructorSpec = MethodSpec.constructorBuilder()
                .addParameters(parameterSpec)
                .addCode(initAllFieldsInConstructor(parameterSpec));

        return constructorSpec.build();
    }

    private CodeBlock initAllFieldsInConstructor(Iterable<ParameterSpec> params) {

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

        for (ParameterSpec spec : params) {
            codeBlockBuilder.add("this.$N = $N;\n", spec.name, spec.name);
        }

        return codeBlockBuilder.build();
    }

    private String parseDataClassName(Element annotatedElement) {
        String dataClassName = annotatedElement.getSimpleName().toString();
        BuilderInterface builderInterface = annotatedElement.getAnnotation(BuilderInterface.class);
        dataClassName = builderInterface.dataClassName().isEmpty() ? dataClassName.replaceFirst("I", "") : builderInterface.dataClassName();
        return dataClassName;
    }


    private Iterable<FieldSpec> createFieldsSpec(TypeElement element) {
        List<FieldSpec> fields = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            if (el.getKind() == ElementKind.METHOD) {
                ParameterSpec parameterSpec = obtainParamSpec(el);
                fields.add(FieldSpec.builder(parameterSpec.type, parameterSpec.name).addModifiers(Modifier.PRIVATE)
                        .build());
            }
        }

        return fields;
    }

    private Iterable<ParameterSpec> createParamSpecs(TypeElement element) {
        List<ParameterSpec> params = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            if (el.getKind() == ElementKind.METHOD) {
                params.add(obtainParamSpec(el));
            }
        }

        return params;
    }

    private MethodSpec getBuilderMethodSpec(TypeElement element) {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("Creates the new builder instance.")
                .returns(ClassName.get(ElementsUtils.parsePackageName(env, element), parseDataClassName(element) + BUILDER_NAME_POSTFIX))
                .addStatement("return new " + parseDataClassName(element) + BUILDER_NAME_POSTFIX + "()")
                .build();
    }

    private MethodSpec getBuildMethodSpec(TypeElement element) {

        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Constructs the new object of type: {@link $T}.", ClassName.get(element))
                .returns(ClassName.get(ElementsUtils.parsePackageName(env, element), parseDataClassName(element)))
                .addCode(createNewObject(element))
                .build();
    }

    private CodeBlock createNewObject(TypeElement element) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        Iterable<ParameterSpec> params = createParamSpecs(element);
        codeBlockBuilder.add("return new $N(", parseDataClassName(element));

        Iterator<ParameterSpec> iterator = params.iterator();

        while (iterator.hasNext()) {
            ParameterSpec param = iterator.next();
            if (iterator.hasNext()) {
                codeBlockBuilder.add("$N,", param.name);
            } else {
                codeBlockBuilder.add("$N", param.name);
            }
        }

        codeBlockBuilder.add(");\n");

        return codeBlockBuilder.build();
    }

    private List<MethodSpec> createMutatorMethodsSpec(TypeElement element) {

        List<MethodSpec> methods = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            if (el.getKind() == ElementKind.METHOD) {

                ParameterSpec parameterSpec = obtainParamSpec(el, "value");

                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(el.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterSpec)
                        .returns(ClassName.get(ElementsUtils.parsePackageName(env, element), parseDataClassName(element) + BUILDER_NAME_POSTFIX))
                        .addStatement("this." + el.getSimpleName().toString() + " = " + parameterSpec.name)
                        .addStatement("return this");

                prepareJavadocsIfAvailable(el, methodSpecBuilder);

                methods.add(methodSpecBuilder.build());
            }
        }

        return methods;
    }

    private void prepareJavadocsIfAvailable(Element el, MethodSpec.Builder methodSpecBuilder) {
        Setter setterAnnotation = el.getAnnotation(Setter.class);
        if (setterAnnotation != null) {
            methodSpecBuilder.addJavadoc(setterAnnotation.jdocs());
        }
    }

    private static ParameterSpec obtainParamSpec(Element element) {
        return obtainParamSpec(element, null);
    }

    private static ParameterSpec obtainParamSpec(Element element, String overrideName) {

        ExecutableElement executableElement = (ExecutableElement) element;

        TypeName type = TypeName.get(executableElement.getReturnType());
        String name = overrideName == null ? element.getSimpleName().toString() : overrideName;

        return ParameterSpec.builder(type, name)
                .build();
    }

}
