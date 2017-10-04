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
        if (descriptor.getElementKind() == ElementKind.CLASS) {
            for (VariableElement var : descriptor.getFields()) {

                if (checkIfFieldForProcessing(var)) {
                    continue;
                }

                DataClassBuilder.MethodDocs builderMethodDocsAnnotation = var.getAnnotation(DataClassBuilder.MethodDocs.class);
                DataClassBuilder.HasDefault hasDefaultAnnotation = var.getAnnotation(DataClassBuilder.HasDefault.class);

                FieldSpec.Builder fieldSpec = FieldSpec.builder(TypeName.get(var.asType()), var.getSimpleName().toString()).addModifiers(Modifier.PRIVATE);

                if (hasDefaultAnnotation != null) {
                    fieldSpec.initializer(CodeBlock.of("$L", hasDefaultAnnotation.value()));
                }

                MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(var.getSimpleName().toString()).addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeName.get(var.asType()), var.getSimpleName().toString()).build())
                        .addStatement("this.$N = $N", var.getSimpleName(), var.getSimpleName())
                        .addStatement("return this")
                        .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()));

                if (builderMethodDocsAnnotation != null) {
                    methodSpec.addJavadoc(builderMethodDocsAnnotation.text() + "\n");
                }

                builderClassSpecBuilder.addField(fieldSpec.build());
                builderClassSpecBuilder.addMethod(methodSpec.build());
            }
        }

        builderClassSpecBuilder.addMethod(createBuilderMethodSpec());
        builderClassSpecBuilder.addMethod(createBuildMethodSpec());
        builderClassSpecBuilder.addMethod(createPrivateConstructor());
    }

    private boolean checkIfFieldForProcessing(VariableElement var) {
        return var.getModifiers().contains(Modifier.FINAL) || var.getModifiers().contains(Modifier.STATIC);
    }

    private MethodSpec createPrivateConstructor() {
        return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
    }

    private MethodSpec createBuilderMethodSpec() {
        return MethodSpec.methodBuilder(descriptor.getAnnotation().builderMethodName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc(descriptor.getAnnotation().builderMethodJDocs() + "\n")
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()))
                .addStatement("return new " + descriptor.getDataClassBuilderName() + "()")
                .build();
    }

    private MethodSpec createBuildMethodSpec() {

        return MethodSpec.methodBuilder(descriptor.getAnnotation().buildMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(descriptor.getAnnotation().buildMethodJDocs() + "\n")
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getTypeElement().getSimpleName().toString()))
                .addCode(createNewObject())
                .build();
    }

    private CodeBlock createNewObject() {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        codeBlockBuilder.add("return new $N(", descriptor.getTypeElement().getSimpleName());
        StringBuilder stringBuilder = new StringBuilder();

        List<VariableElement> elements = descriptor.getFields();

        for (int i = 0; i < elements.size(); i++) {

            VariableElement variableElement = elements.get(i);
            if (checkIfFieldForProcessing(variableElement)) {
                continue;
            }

            stringBuilder.append(String.format("%s,", variableElement.getSimpleName().toString()));
        }

        int last = stringBuilder.lastIndexOf(",");
        codeBlockBuilder.add(stringBuilder.deleteCharAt(last).toString());
        codeBlockBuilder.add(");\n");

        return codeBlockBuilder.build();
    }
}
