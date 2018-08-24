package eu.rsulkowski.jdoocsoup.processor.handler;

import com.squareup.javapoet.*;
import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import eu.rsulkowski.jdoocsoup.processor.descriptor.DataClassBuilderDescriptor;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;

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

        List<VariableElement> requiredElements = new ArrayList<>();

        if (descriptor.getElementKind() == ElementKind.CLASS) {

            for (VariableElement variableElement : descriptor.getFields()) {

                if (checkIfFieldForProcessing(variableElement)) {
                    continue;
                }

                DataClassBuilder.MethodDocs builderMethodDocsAnnotation = variableElement.getAnnotation(DataClassBuilder.MethodDocs.class);
                DataClassBuilder.HasDefault hasDefaultAnnotation = variableElement.getAnnotation(DataClassBuilder.HasDefault.class);
                DataClassBuilder.Required isRequired = variableElement.getAnnotation(DataClassBuilder.Required.class);

                FieldSpec.Builder fieldSpec = FieldSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).addModifiers(Modifier.PRIVATE);

                if (hasDefaultAnnotation != null) {
                    fieldSpec.initializer(CodeBlock.of("$L", hasDefaultAnnotation.value()));
                }

                if (isRequired == null) {
                    List<MethodSpec.Builder> methodSpecs = createBuilderSetterMethodFor(variableElement, builderMethodDocsAnnotation);
                    for (MethodSpec.Builder methodSpec : methodSpecs) {
                        builderClassSpecBuilder.addMethod(methodSpec.build());
                    }
                } else {
                    requiredElements.add(variableElement);
                }
                builderClassSpecBuilder.addField(fieldSpec.build());
            }
        }

        List<ParameterSpec> requiredParams = parseRequiredParams(requiredElements);

        builderClassSpecBuilder.addMethod(createBuilderMethodSpec(requiredParams));
        builderClassSpecBuilder.addMethod(createBuildMethodSpec());
        builderClassSpecBuilder.addMethod(createPrivateConstructor(requiredParams));
    }

    private List<MethodSpec.Builder> createBuilderSetterMethodFor(VariableElement variableElement, DataClassBuilder.MethodDocs builderMethodDocsAnnotation) {
        List<MethodSpec.Builder> methods = new ArrayList<>();
        String setterMethodName = variableElement.getSimpleName().toString();

        if (!descriptor.getSetterPrefix().isEmpty()) {
            setterMethodName = descriptor.getSetterPrefix() + StringUtils.capitalize(setterMethodName);
        }

        String varTypeName = variableElement.asType().toString();
        if (isListWithItems(setterMethodName, varTypeName)){
            MethodSpec.Builder methodSpecListItem = methodForListItem(variableElement, builderMethodDocsAnnotation, setterMethodName, varTypeName);
            methods.add(methodSpecListItem);
        }

        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(setterMethodName).addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build())
                .addStatement("this.$N = $N", variableElement.getSimpleName(), variableElement.getSimpleName())
                .addStatement("return this")
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()));

        if (builderMethodDocsAnnotation != null) {
            methodSpec.addJavadoc(builderMethodDocsAnnotation.value() + "\n");
        }
        methods.add(methodSpec);
        return methods;
    }

    private MethodSpec.Builder methodForListItem(VariableElement variableElement, DataClassBuilder.MethodDocs builderMethodDocsAnnotation, String setterMethodName, String varTypeName) {
        String listTypeName = varTypeName.substring(varTypeName.indexOf("<")+1,varTypeName.indexOf(">"));
        String methodName = setterMethodName.substring(0, setterMethodName.length()-1);
        String paramName = variableElement.getSimpleName().toString();
        String oneParamName = paramName.substring(0, paramName.length()-1);

        MethodSpec.Builder methodSpecListItem = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(listTypeName), oneParamName).build())
                .addStatement("if (this.$N == null) { $N = new java.util.ArrayList();}", variableElement.getSimpleName(), variableElement.getSimpleName())
                .addStatement("this.$N.add($N)", variableElement.getSimpleName(), oneParamName)
                .addStatement("return this")
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()));
        if (builderMethodDocsAnnotation != null) {
            methodSpecListItem.addJavadoc(builderMethodDocsAnnotation.value() + "\n");
        }
        return methodSpecListItem;
    }

    private boolean isListWithItems(String setterMethodName, String varTypeName) {
        return varTypeName.startsWith("java.util.List") && setterMethodName.endsWith("s");
    }

    private List<ParameterSpec> parseRequiredParams(List<VariableElement> requiredElements) {
        List<ParameterSpec> requiredParams = new ArrayList<>();

        for (VariableElement variableElement : requiredElements) {
            requiredParams.add(ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build());
        }

        return requiredParams;
    }

    private boolean checkIfFieldForProcessing(VariableElement variableElement) {
        return variableElement.getModifiers().contains(Modifier.FINAL) || variableElement.getModifiers().contains(Modifier.STATIC) || variableElement.getAnnotation(DataClassBuilder.Ignored.class) != null;
    }

    private MethodSpec createPrivateConstructor(List<ParameterSpec> requiredElements) {
        return MethodSpec.constructorBuilder()
                .addParameters(requiredElements)
                .addCode(assignRequiredFields(requiredElements))
                .addModifiers(descriptor.isPublicBuildConstructor() ? Modifier.PUBLIC : Modifier.PRIVATE)
                .build();
    }

    private String assignRequiredFields(List<ParameterSpec> requiredElements) {
        if (requiredElements.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<requiredElements.size(); i++) {
            sb.append("this.").append(requiredElements.get(i).name)
                    .append("=").append(requiredElements.get(i).name)
                    .append(";\n");
        }
        return sb.toString();
    }

    private MethodSpec createBuilderMethodSpec(List<ParameterSpec> requiredElements) {
        return MethodSpec.methodBuilder(descriptor.getAnnotation().builderMethodName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc(descriptor.getAnnotation().builderMethodJDocs() + "\n")
                .addParameters(requiredElements)
                .returns(ClassName.get(descriptor.getPackageName(), descriptor.getDataClassBuilderName()))
                .addStatement("return new " + descriptor.getDataClassBuilderName() + getRequiredParamsString(requiredElements))
                .build();
    }

    private String getRequiredParamsString(List<ParameterSpec> requiredElements) {
        if (requiredElements.isEmpty()) {
            return "()";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < requiredElements.size(); i++) {
            if (i == requiredElements.size() - 1) {
                sb.append(requiredElements.get(i).name);
            } else {
                sb.append(requiredElements.get(i).name).append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private MethodSpec createBuildMethodSpec() {
        ClassName forcedReturnType = ClassName.get(descriptor.getPackageName(), descriptor.getTypeElement().getSimpleName().toString());

        if (!ClassName.get(descriptor.getBuildMethodReturnType()).equals(ClassName.get(Class.class))) {
            forcedReturnType = ClassName.get(descriptor.getBuildMethodReturnType());
        }

        return MethodSpec.methodBuilder(descriptor.getAnnotation().buildMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(descriptor.getAnnotation().buildMethodJDocs() + "\n")
                .returns(forcedReturnType)
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
        if (last == -1){
            codeBlockBuilder.add(stringBuilder.toString());
        }else {
            codeBlockBuilder.add(stringBuilder.deleteCharAt(last).toString());
        }
        codeBlockBuilder.add(");\n");

        return codeBlockBuilder.build();
    }
}
