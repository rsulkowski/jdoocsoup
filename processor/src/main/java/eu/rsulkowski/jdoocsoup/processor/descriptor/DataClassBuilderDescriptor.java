package eu.rsulkowski.jdoocsoup.processor.descriptor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import eu.rsulkowski.jdoocsoup.processor.utils.ElementsUtils;
import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import java.util.ArrayList;
import java.util.List;

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
    private final String setterPrefix;
    private final ProcessingEnvironment processingEnvironment;
    private final TypeElement typeElement;
    private final ElementKind elementKind;
    private final TypeSpec.Builder typeSpecBuilder;
    private final List<ExecutableElement> methods = new ArrayList<>();
    private final List<VariableElement> fields = new ArrayList<>();
    private TypeElement buildMethodReturnType;

    public DataClassBuilderDescriptor(ProcessingEnvironment env, TypeElement element) {
        annotation = element.getAnnotation(DataClassBuilder.class);
        try {
            annotation.buildMethodReturnType();
        } catch (MirroredTypeException mte) {
            buildMethodReturnType = (TypeElement) env.getTypeUtils().asElement(mte.getTypeMirror());
        }

        this.dataClassBuilderName = annotation.name().isEmpty() ? element.getSimpleName() + BUILDER_NAME_POSTFIX : annotation.name();
        this.setterPrefix = annotation.setterPrefix();
        this.packageName = ElementsUtils.parsePackageName(env, element);
        this.processingEnvironment = env;
        this.typeElement = element;
        this.elementKind = element.getKind();
        this.typeSpecBuilder = TypeSpec.classBuilder(dataClassBuilderName);
        for (String superInterface : annotation.implementInterfaces()){
            TypeName superI = ClassName.bestGuess(superInterface);
            typeSpecBuilder.addSuperinterface(superI);
        }
        typeSpecBuilder.addModifiers(Modifier.PUBLIC);
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
