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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import eu.rsulkowski.jdoocsoup.processor.handler.BaseAnnotationHandler;
import eu.rsulkowski.jdoocsoup.processor.handler.BuilderInterfaceHandler;
import eu.rsulkowski.jdoocsoup.processor.handler.SetterHandler;
import eu.rsulkowski.jdoocsoup.processor.utils.Pair;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "eu.rsulkowski.jdoocsoup.annotation.BuilderInterface"
})
@AutoService(Processor.class)
public class JDoocsSoupAnnotationProcessor extends AbstractProcessor implements BaseAnnotationHandler.AnnotationHandlerCallback {


    private BuilderInterfaceHandler builderInterfaceHandler;
    private SetterHandler setterHandler;
    private List<Pair<String, TypeSpec>> javaClassesToBeCreated = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        builderInterfaceHandler = new BuilderInterfaceHandler(this, processingEnvironment);
        setterHandler = new SetterHandler(this, processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        builderInterfaceHandler.process(roundEnvironment, this);
        setterHandler.process(roundEnvironment, this);

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
