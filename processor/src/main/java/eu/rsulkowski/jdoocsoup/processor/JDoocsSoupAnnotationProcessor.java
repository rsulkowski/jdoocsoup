package eu.rsulkowski.jdoocsoup.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import eu.rsulkowski.jdoocsoup.annotation.BuilderInterface;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("eu.rsulkowski.jdoocsoup.annotation.BuilderInterface")
@AutoService(Processor.class)
public class JDoocsSoupAnnotationProcessor extends AbstractProcessor {

    private final static String BUILDER_NAME_POSTFIX = "Builder";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(BuilderInterface.class)) {

            if (annotatedElement.getKind() == ElementKind.INTERFACE) {
                TypeElement element = (TypeElement) annotatedElement;

                TypeSpec builderClassSpec = prepareDataClassBuilderSpec(annotatedElement, element);
                TypeSpec dataClassSpec = prepareDataClassSpec(annotatedElement, element);

                createJavaFileForTypeSpec(element, dataClassSpec);
                createJavaFileForTypeSpec(element, builderClassSpec);
            }
        }
        return true;
    }

    private void createJavaFileForTypeSpec(TypeElement element, TypeSpec builderClassSpec) {
        JavaFile javaFile = JavaFile.builder(parsePackageName(element), builderClassSpec)
                .build();

        createJavaFile(javaFile);
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
                .returns(ClassName.get(parsePackageName(element), parseDataClassName(element) + BUILDER_NAME_POSTFIX))
                .addStatement("return new " + parseDataClassName(element) + BUILDER_NAME_POSTFIX + "()")
                .build();
    }

    private MethodSpec getBuildMethodSpec(TypeElement element) {

        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Constructs the new object of type: {@link $T}.", ClassName.get(element))
                .returns(ClassName.get(parsePackageName(element), parseDataClassName(element)))
                .addCode(createNewObject(element))
                .build();
    }

    private CodeBlock createNewObject(TypeElement element) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        Iterable<ParameterSpec> params = createParamSpecs(element);
        codeBlockBuilder.add("return new $N(", parseDataClassName(element));

        Iterator<ParameterSpec> iterator = params.iterator();

        while(iterator.hasNext()) {
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
                        .returns(ClassName.get(parsePackageName(element), parseDataClassName(element) + BUILDER_NAME_POSTFIX))
                        .addStatement("this." + el.getSimpleName().toString() + " = " + parameterSpec.name)
                        .addStatement("return this");

                prepareJavadocsIfAvailable(el, methodSpecBuilder);

                methods.add(methodSpecBuilder.build());
            }
        }

        return methods;
    }

    private void prepareJavadocsIfAvailable(Element el, MethodSpec.Builder methodSpecBuilder) {
        BuilderInterface.Setter setterAnnotation = el.getAnnotation(BuilderInterface.Setter.class);
        if (setterAnnotation != null) {
            methodSpecBuilder.addJavadoc(setterAnnotation.description());
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

    private String parsePackageName(TypeElement element) {
        return processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    private void createJavaFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
