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
    /**
     * Describes the name of the Builder.
     * If left empty than the default name of Builder will contain
     * the name of the annotated class + postfix: "Builder".
     */
    String name() default "";

    /**
     * Contains the JavaDocs of the whole Builder class.
     */
    String jdocs() default "";

    /**
     * Specifies the name of the factory method to create Builder. By default it is: "builder"
     */
    String builderMethodName() default "builder";

    /**
     * Specifies the name of the terminal method to produce the new data class object. By default it is: "build"
     */
    String buildMethodName() default "build";

    /**
     * Contains the JavaDocs for the builder method.
     */
    String builderMethodJDocs() default "";

    /**
     * Contains the JavaDocs for the build method.
     */
    String buildMethodJDocs() default "";

    /**
     * Defines the annotation which adds javadocs for Builder mutator methods.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.METHOD, ElementType.FIELD})
    @interface MethodDocs {
        /**
         * The text of the javadocs which will describe the Builder mutator method.
         */
        String text() default "";
    }

}
