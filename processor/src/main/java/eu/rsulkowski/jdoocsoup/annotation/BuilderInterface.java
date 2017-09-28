package eu.rsulkowski.jdoocsoup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BuilderInterface {

    String dataClassName() default "";

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface Setter {
        String description() default "";
    }
}
