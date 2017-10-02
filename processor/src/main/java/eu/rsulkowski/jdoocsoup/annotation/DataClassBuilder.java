package eu.rsulkowski.jdoocsoup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rsulkowski on 02/10/2017.
 */

public @interface DataClassBuilder {
    String name() default "";
    String jdocs() default "";

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface MethodDocs {
        String text() default "";
    }

}
