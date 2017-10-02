package eu.rsulkowski.jdoocsoup.processor.utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by sulkowsk on 29/09/2017.
 */

public class ElementsUtils {

    public static String parsePackageName(ProcessingEnvironment env, TypeElement element) {
        return env.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }
}
