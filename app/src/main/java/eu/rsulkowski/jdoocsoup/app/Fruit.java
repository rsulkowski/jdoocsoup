package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

@Getter
public abstract class Fruit extends Size {

    @DataClassBuilder.MethodDocs("Some javadocs for weight.")
    protected Integer weight;

    protected String color;

    @DataClassBuilder.Ignored
    protected String tagToBeIgnored;
}
