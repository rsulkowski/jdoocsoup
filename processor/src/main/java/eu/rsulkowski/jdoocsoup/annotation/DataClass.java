package eu.rsulkowski.jdoocsoup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rsulkowski on 02/10/2017.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DataClass {

    String name() default "";

    String jdocs() default "";

    String ext() default "";

    String impl() default "";


    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    @interface Getters {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    @interface Setters {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface SetterDocs {
        String text() default "";
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface GetterDocs {
        String text() default "";

        String returnText() default "";
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface Method {
        String[] params() default {};
    }

}
