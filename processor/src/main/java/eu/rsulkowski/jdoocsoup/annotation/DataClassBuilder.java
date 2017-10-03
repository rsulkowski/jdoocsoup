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
public @interface DataClassBuilder {
    String name() default "";
    String jdocs() default "";

    String builderMethodName() default "builder";
    String buildMethodName() default "build";

    String builderMethodJDocs() default "";
    String buildMethodJDocs() default "";

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.METHOD, ElementType.FIELD})
    @interface MethodDocs {
        String text() default "";
    }

}
