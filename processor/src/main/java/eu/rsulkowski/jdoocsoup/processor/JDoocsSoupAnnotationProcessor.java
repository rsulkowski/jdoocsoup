package eu.rsulkowski.jdoocsoup.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import eu.rsulkowski.jdoocsoup.annotation.Builder;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("eu.rsulkowski.jdoocsoup.annotation.Builder")
public class JDoocsSoupAnnotationProcessor extends AbstractProcessor {

    private final static String BUILDER_NAME_POSTFIX = "Builder";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Builder.class)) {

            if (annotatedElement.getKind() == ElementKind.CLASS) {
                TypeElement element = (TypeElement) annotatedElement;

                MethodSpec builder = getBuilderMethodSpec(element);
                MethodSpec build = getBuildMethodSpec(element);

                TypeSpec helloWorld = TypeSpec.classBuilder(annotatedElement.getSimpleName() + BUILDER_NAME_POSTFIX)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(builder)
                        .addMethod(build)
                        .addMethods(createMutatorMethodsSpec(element))
                        .addFields(createFieldsSpec(element))
                        .build();

                JavaFile javaFile = JavaFile.builder(parsePackageName(element), helloWorld)
                        .build();

                createJavaFile(javaFile);
            }
        }
        return true;
    }

    private Iterable<FieldSpec> createFieldsSpec(TypeElement element) {
        List<FieldSpec> fields = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            if (el.getKind() == ElementKind.FIELD) {
                ParameterSpec parameterSpec = obtainParamSpec((VariableElement) el);
                fields.add(FieldSpec.builder(parameterSpec.type, parameterSpec.name).addModifiers(Modifier.PRIVATE)
                        .build());
            }
        }

        return fields;
    }

    private MethodSpec getBuilderMethodSpec(TypeElement element) {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(parsePackageName(element), element.getSimpleName() + BUILDER_NAME_POSTFIX))
                .addStatement("return new " + element.getSimpleName() + BUILDER_NAME_POSTFIX + "()")
                .build();
    }

    private MethodSpec getBuildMethodSpec(TypeElement element) {

        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(parsePackageName(element), element.getSimpleName().toString()))
                .addStatement("return new " + element.getSimpleName() + "()")
                .build();
    }

    private List<MethodSpec> createMutatorMethodsSpec(TypeElement element) {

        List<MethodSpec> methods = new ArrayList<>();

        for (Element el : element.getEnclosedElements()) {
            if (el.getKind() == ElementKind.FIELD) {
                ParameterSpec parameterSpec = obtainParamSpec((VariableElement) el, "value");
                methods.add(MethodSpec.methodBuilder(el.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterSpec)
                        .returns(ClassName.get(parsePackageName(element), element.getSimpleName() + BUILDER_NAME_POSTFIX))
                        .addStatement("this." + el.getSimpleName().toString() + " = " + parameterSpec.name)
                        .addStatement("return this")
                        .build());
            }
        }

        return methods;
    }

    private static ParameterSpec obtainParamSpec(VariableElement element) {
        return obtainParamSpec(element, null);
    }

    private static ParameterSpec obtainParamSpec(VariableElement element, String overrideName) {
        TypeName type = TypeName.get(element.asType());
        String name = overrideName == null ? element.getSimpleName().toString() : overrideName;
        return ParameterSpec.builder(type, name)
                .addModifiers(element.getModifiers())
                .build();
    }

    private String parsePackageName(TypeElement element) {
        return processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    private void createJavaFile(JavaFile javaFile) {
        try {

            javaFile.writeTo(new File("build/generated/jdoocs"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
