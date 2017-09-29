package eu.rsulkowski.jdoocsoup.processor.handler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import eu.rsulkowski.jdoocsoup.annotation.Setter;

/**
 * Created by sulkowsk on 29/09/2017.
 */

public class SetterHandler extends BaseAnnotationHandler {

    public SetterHandler(AbstractProcessor processor, ProcessingEnvironment processingEnvironment) {
        super(processor, processingEnvironment, Setter.class, ElementKind.METHOD);
    }

    @Override
    public void process(RoundEnvironment roundEnvironment, AnnotationHandlerCallback callback) {
        processForApplicable(roundEnvironment, new AnnotationAction() {
            @Override
            public void execute(Element element) {

            }
        });
    }
}
