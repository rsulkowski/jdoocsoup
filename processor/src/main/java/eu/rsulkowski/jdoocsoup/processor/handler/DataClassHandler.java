package eu.rsulkowski.jdoocsoup.processor.handler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import eu.rsulkowski.jdoocsoup.annotation.DataClass;

/**
 * Created by rsulkowski on 29/09/2017.
 */
public class DataClassHandler extends BaseAnnotationHandler<DataClass> {

    public DataClassHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment) {
        super(processor, processingEnvironment, DataClass.class, ElementKind.INTERFACE);
    }

    @Override
    public void process(RoundEnvironment roundEnvironment, final AnnotationHandlerCallback callback) {
        processForApplicable(roundEnvironment, new AnnotationAction() {
                    @Override
                    public void execute(Element element) {
//                        TypeElement typeElement = (TypeElement) element;
//                        TypeSpec dataClassSpec = prepareDataClassSpec(element, typeElement);
//                        callback.onTypeSpecsCreated(new Pair(ElementsUtils.parsePackageName(env, typeElement), dataClassSpec));
                    }
                }
        );
    }

//    private TypeSpec prepareDataClassSpec(Element annotatedElement, TypeElement element) {
//
//        String dataClassName = parseDataClassName(annotatedElement);
//
//        return TypeSpec.classBuilder(dataClassName)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addMethod(createDataClassConstructor(annotatedElement))
//                .addFields(createFieldsSpec(element))
//                .build();
//    }
//
//    private MethodSpec createDataClassConstructor(Element annotatedElement) {
//        TypeElement element = (TypeElement) annotatedElement;
//        Iterable<ParameterSpec> parameterSpec = createParamSpecs(element);
//
//
//        MethodSpec.Builder constructorSpec = MethodSpec.constructorBuilder()
//                .addParameters(parameterSpec)
//                .addCode(initAllFieldsInConstructor(parameterSpec));
//
//        return constructorSpec.build();
//    }
//
//    private CodeBlock initAllFieldsInConstructor(Iterable<ParameterSpec> params) {
//
//        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
//
//        for (ParameterSpec spec : params) {
//            codeBlockBuilder.add("this.$N = $N;\n", spec.name, spec.name);
//        }
//
//        return codeBlockBuilder.build();
//    }
}


