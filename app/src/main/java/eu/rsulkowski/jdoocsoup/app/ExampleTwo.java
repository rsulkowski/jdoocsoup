package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;

/**
 * Created by rsulkowski on 02/10/2017.
 */
@DataClassBuilder(
        jdocs = "A class created as an ExampleTwo",
        builderMethodJDocs = "Builder method javadocs",
        buildMethodJDocs = "Build method javadocs")
public class ExampleTwo {

    @DataClassBuilder.MethodDocs(text = "This method sets the value for the ExampleTwo")
    int value;

    @DataClassBuilder.MethodDocs(text = "This method sets the nameOfSth for the ExampleTwo")
    String nameOfSth;

    @DataClassBuilder.MethodDocs(text = "This method sets the valueInLong for the ExampleTwo")
    long valueInLong;

    ExampleTwo(int value, String nameOfSth, long valueInLong) {
        this.value = value;
        this.nameOfSth = nameOfSth;
        this.valueInLong = valueInLong;
    }
}
