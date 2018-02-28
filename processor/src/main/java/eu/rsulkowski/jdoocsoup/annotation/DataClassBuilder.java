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
     *  @return name
     */
    String name() default "";

    /**
     * Contains the JavaDocs of the whole Builder class.
     *  @return jdocs
     */
    String jdocs() default "";

    /**
     * Specifies the name of the factory method to create Builder. By default it is: "create"
     *  @return builderMethodName
     */
    String builderMethodName() default "create";

    /**
     * Specifies the name of the terminal method to produce the new data class object. By default it is: "build"
     *  @return buildMethodName
     */
    String buildMethodName() default "build";

    /**
     * Specifies the type which is returned by the build method instead of the built type. Typically, it might be an interface.
     * @return buildMethodReturnType
     */
    Class buildMethodReturnType() default Class.class;

    /**
     * Contains the JavaDocs for the builder method.
     * @return builderMethodJDocs
     */
    String builderMethodJDocs() default "";

    /**
     * Contains the JavaDocs for the build method.
     * @return buildMethodJDocs
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
         * @return the value
         */
        String value() default "";
    }

    /**
     * Defines that the field at DataClass has default value and it should be used if not set via builder.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD})
    @interface HasDefault {
        /**
         * Accepts literals.
         * @return value
         */
        String value();
    }

    /**
     * Defines that the field at DataClass has default value and it should be used if not set via builder.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD})
    @interface Ignored {
    }

}
