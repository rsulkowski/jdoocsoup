package eu.rsulkowski.jdoocsoup.processor.descriptor;

import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import lombok.Getter;

/**
 * Created by rsulkowski on 10/2/17.
 */

/**
 * Contains the description of the DataClassBuilder such as: package name, element kind etc.
 */
@Getter
public class DataClassBuilderDescriptor {

    private final static String BUILDER_NAME_POSTFIX = "Builder";

    private final DataClassBuilder annotation;
    private final String dataClassBuilderName;
    private final String packageName;
    private final ProcessingEnvironment processingEnvironment;
    private final TypeElement typeElement;
    private final ElementKind elementKind;
    private final TypeSpec.Builder typeSpecBuilder;
    private final List<ExecutableElement> methods = new ArrayList<>();
    private final List<VariableElement> fields = new ArrayList<>();

    public DataClassBuilderDescriptor(ProcessingEnvironment env, TypeElement element) {
        annotation = element.getAnnotation(DataClassBuilder.class);
        dataClassBuilderName = annotation.name().isEmpty() ? element.getSimpleName() + BUILDER_NAME_POSTFIX : annotation.name();
        packageName = ElementsUtils.parsePackageName(env, element);
        this.processingEnvironment = env;
        this.typeElement = element;
        this.elementKind = element.getKind();
        this.typeSpecBuilder = TypeSpec.classBuilder(dataClassBuilderName).addModifiers(Modifier.PUBLIC);
        parseAll();
    }

    private void parseAll() {
        typeSpecBuilder.addJavadoc(annotation.jdocs() + "\n");

        checkElementForMembers(typeElement);

        if (typeElement.getSuperclass() == null) {
            return;
        }

        Element superClassElement = processingEnvironment.getTypeUtils().asElement(typeElement.getSuperclass());
        checkElementForMembers(superClassElement);
    }

    private void checkElementForMembers(Element rootElement) {
        for (Element element : rootElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                methods.add((ExecutableElement) element);
            } else if (element.getKind() == ElementKind.FIELD) {
                fields.add((VariableElement) element);
            }
        }
    }
}
