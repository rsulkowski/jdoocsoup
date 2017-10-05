package eu.rsulkowski.jdoocsoup.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import eu.rsulkowski.jdoocsoup.processor.handler.BaseAnnotationHandler;
import eu.rsulkowski.jdoocsoup.processor.handler.DataClassBuilderHandler;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;

@SupportedAnnotationTypes({
        "eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder"})
@AutoService(Processor.class)
public class JDoocsSoupAnnotationProcessor extends AbstractProcessor implements BaseAnnotationHandler.AnnotationHandlerCallback {

    private DataClassBuilderHandler dataClassBuilderHandler;
    private List<Pair<String, TypeSpec>> javaClassesToBeCreated = new ArrayList<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        dataClassBuilderHandler = new DataClassBuilderHandler(this, processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        dataClassBuilderHandler.process(roundEnvironment, this);

        // The final work to be done.
        generateAllJavaClases();
        javaClassesToBeCreated.clear();
        return true;
    }

    @Override
    public void onTypeSpecsCreated(Pair<String, TypeSpec> packageAndTypeSpec) {
        javaClassesToBeCreated.add(packageAndTypeSpec);
    }

    private void generateAllJavaClases() {
        for (Pair<String, TypeSpec> fileDesc : javaClassesToBeCreated) {
            createJavaFile(fileDesc.getFirst(), fileDesc.getSecond());
        }
    }

    private void createJavaFile(String packageName, TypeSpec typeSpec) {
        try {
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .build();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException ex) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "It was not possible to generate java files due to an error: \n" + ex.getMessage());
        }
    }
}
