package eu.rsulkowski.jdoocsoup.processor.handler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import eu.rsulkowski.jdoocsoup.processor.descriptor.DataClassBuilderDescriptor;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;

/**
 * Created by rsulkowski on 29/09/2017.
 */
public class DataClassBuilderHandler extends BaseAnnotationHandler<DataClassBuilder> {


    private DataClassBuilderDescriptor descriptor;
    private TypeSpec.Builder builderClassSpecBuilder;

    public DataClassBuilderHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment) {
        super(processor, processingEnvironment, DataClassBuilder.class, ElementKind.INTERFACE, ElementKind.CLASS);
    }

    public void process(final RoundEnvironment roundEnvironment, final AnnotationHandlerCallback callback) {
        processForApplicable(roundEnvironment, new BaseAnnotationHandler.AnnotationAction() {
            public void execute(Element annotatedElement) {
                TypeElement element = (TypeElement) annotatedElement;
                descriptor = new DataClassBuilderDescriptor(env, element);
                builderClassSpecBuilder = descriptor.getTypeSpecBuilder();
                parseDataFromDescriptor();
                callback.onTypeSpecsCreated(new Pair(ElementsUtils.parsePackageName(env, element), builderClassSpecBuilder.build()));
            }
        });
    }

    private void parseDataFromDescriptor() {
        if (descriptor.getElementKind() == ElementKind.INTERFACE) {
            // TODO: NOT YET SUPPORTED

//            for (ExecutableElement var : descriptor.getMethods()) {
//
//                DataClassBuilder.MethodDocs setterDocsAnnotation = var.getAnnotation(DataClassBuilder.MethodDocs.class);
//
//                FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(var.getReturnType()), var.getSimpleName().toString()).build();
//
//                MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(var.getSimpleName().toString())
//                        .addParameter(ParameterSpec.builder(TypeName.get(var.getReturnType()), var.getSimpleName().toString()).build())
//                        .addStatement("this.$N=$N", var.getSimpleName(), var.getSimpleName())
//                        .addStatement("return new " + descriptor.getDataClassBuilderName() + "()")
//                        .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()));
//
//                if (setterDocsAnnotation != null) {
//                    methodSpec.addJavadoc(setterDocsAnnotation.text());
//                }
//
//                builderClassSpecBuilder.addField(fieldSpec);
//                builderClassSpecBuilder.addMethod(methodSpec.build());
//            }
        } else if (descriptor.getElementKind() == ElementKind.CLASS) {
            for (VariableElement var : descriptor.getFields()) {

                DataClassBuilder.MethodDocs builderMethodDocsAnnotation = var.getAnnotation(DataClassBuilder.MethodDocs.class);

                FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(var.asType()), var.getSimpleName().toString()).addModifiers(Modifier.PRIVATE).build();

                MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(var.getSimpleName().toString()).addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeName.get(var.asType()), var.getSimpleName().toString()).build())
                        .addStatement("this.$N = $N", var.getSimpleName(), var.getSimpleName())
                        .addStatement("return this")
                        .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()));

                if (builderMethodDocsAnnotation != null) {
                    methodSpec.addJavadoc(builderMethodDocsAnnotation.text());
                }

                builderClassSpecBuilder.addField(fieldSpec);
                builderClassSpecBuilder.addMethod(methodSpec.build());
            }
        }

        builderClassSpecBuilder.addMethod(createBuilderMethodSpec());
        builderClassSpecBuilder.addMethod(createBuildMethodSpec());
        builderClassSpecBuilder.addMethod(createPrivateConstructor());
    }

    private MethodSpec createPrivateConstructor() {
        return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
    }

    private MethodSpec createBuilderMethodSpec() {
        return MethodSpec.methodBuilder(descriptor.getAnnotation().builderMethodName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc(descriptor.getAnnotation().builderMethodJDocs())
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()))
                .addStatement("return new " + descriptor.getDataClassBuilderName() + "()")
                .build();
    }

    private MethodSpec createBuildMethodSpec() {

        return MethodSpec.methodBuilder(descriptor.getAnnotation().buildMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(descriptor.getAnnotation().buildMethodJDocs())
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getTypeElement().getSimpleName().toString()))
                .addCode(createNewObject())
                .build();
    }

    private CodeBlock createNewObject() {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        codeBlockBuilder.add("return new $N(", descriptor.getTypeElement().getSimpleName());

        List<VariableElement> elements = descriptor.getFields();

        for (int i = 0; i < elements.size(); i++) {

            VariableElement variableElement = elements.get(i);
            if (i < elements.size() - 1) {
                codeBlockBuilder.add("$N,", variableElement.getSimpleName().toString());
            } else {
                codeBlockBuilder.add("$N", variableElement.getSimpleName().toString());
            }
        }

        codeBlockBuilder.add(");\n");

        return codeBlockBuilder.build();
    }
}
