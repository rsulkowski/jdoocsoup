package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

/**
 * Created by rsulkowski on 2017-10-23.
 */
@Getter
abstract class Fruit  {

    @DataClassBuilder.MethodDocs("Some javadocs for weight.")
    protected int weight;

    protected String color;
}
