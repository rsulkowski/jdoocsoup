package eu.rsulkowski.jdoocsoup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sulkowsk on 29/09/2017.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Getter {
    String jdocs() default "";
    String jdocsReturn() default "";
}
