package eu.rsulkowski.jdoocsoup.processor.handler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;

/**
 * Created by rsulkowski on 29/09/2017.
 */
public class DataClassBuilderHandler extends BaseAnnotationHandler<DataClassBuilder> {


    public DataClassBuilderHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment) {
        super(processor, processingEnvironment, DataClassBuilder.class, ElementKind.INTERFACE, ElementKind.CLASS);
    }

    public void process(RoundEnvironment roundEnvironment, final AnnotationHandlerCallback callback) {
        processForApplicable(roundEnvironment, new BaseAnnotationHandler.AnnotationAction() {
            public void execute(Element annotatedElement) {
                TypeElement element = (TypeElement) annotatedElement;
                TypeSpec builderClassSpec = prepareDataClassBuilderSpec(annotatedElement, element);
                callback.onTypeSpecsCreated(new Pair(ElementsUtils.parsePackageName(env, element), builderClassSpec));
            }
        });
    }

    private TypeSpec prepareDataClassBuilderSpec(Element annotatedElement, TypeElement element) {
        MethodSpec builder = getBuilderMethodSpec(element);
        MethodSpec build = getBuildMethodSpec(element);

        return TypeSpec.classBuilder(parseDataClassBuilderName(annotatedElement))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(builder)
                .addMethod(build)
                .addMethods(createMutatorMethodsSpec(element))
                .addFields(createFieldsSpec(element))
                .build();
    }


    private MethodSpec getBuilderMethodSpec(TypeElement element) {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("Creates the new builder instance.")
                .returns(ClassName.get(ElementsUtils.parsePackageName(env, element), parseDataClassBuilderName(element)))
                .addStatement("return new " + parseDataClassBuilderName(element) + "()")
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

                ParameterSpec parameterSpec = obtainParamSpec(el, "value", element.getKind());

                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(el.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterSpec)
                        .returns(ClassName.get(ElementsUtils.parsePackageName(env, element), parseDataClassBuilderName(element)))
                        .addStatement("this." + el.getSimpleName().toString() + " = " + parameterSpec.name)
                        .addStatement("return this");

                prepareJavadocsIfAvailable(el, methodSpecBuilder);

                methods.add(methodSpecBuilder.build());
            }
        }

        return methods;
    }

    private void prepareJavadocsIfAvailable(Element el, MethodSpec.Builder methodSpecBuilder) {
        DataClassBuilder.MethodDocs setterAnnotation = el.getAnnotation(DataClassBuilder.MethodDocs.class);
        if (setterAnnotation != null) {
            methodSpecBuilder.addJavadoc(setterAnnotation.text());
        }
    }


}
