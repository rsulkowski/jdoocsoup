package eu.rsulkowski.jdoocsoup.processor.handler;

import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import eu.rsulkowski.jdoocsoup.processor.utils.Pair;


/**
 * Created by sulkowsk on 29/09/2017.
 */
public abstract class BaseAnnotationHandler<T extends Annotation> {

    protected final AbstractProcessor processor;
    protected final ProcessingEnvironment env;
    protected final Class<T> handledAnnotation;
    protected final ElementKind handledKind;
    ;

    public BaseAnnotationHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment, Class<T> handledAnnotation, ElementKind handledKind) {
        this.processor = processor;
        this.env = processingEnvironment;
        this.handledAnnotation = handledAnnotation;
        this.handledKind = handledKind;
    }

    public abstract void process(RoundEnvironment roundEnvironment, AnnotationHandlerCallback callback);

    protected void processForApplicable(RoundEnvironment roundEnvironment, AnnotationAction action) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(handledAnnotation)) {
            if (annotatedElement.getKind() == handledKind) {
                action.execute(annotatedElement);
            }
        }
    }

    public interface AnnotationAction {
        void execute(Element element);
    }

    public interface AnnotationHandlerCallback {
        void onTypeSpecsCreated(Pair<String, TypeSpec> packageAndTypeSpec);
    }
}
