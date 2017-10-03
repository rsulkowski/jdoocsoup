package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;

/**
 * Created by rsulkowski on 02/10/2017.
 */
@DataClassBuilder(
        jdocs = "POJO class which represents the Person.",
        builderMethodJDocs = "Creates the new builder object for Person",
        buildMethodJDocs = "Gather all passed information from PersonBuilder and creates new Person object")
public class Person {

    @DataClassBuilder.MethodDocs(text = "This method sets the age of the person. Normally from 0 to 130.")
    private int age;

    @DataClassBuilder.MethodDocs(text = "This method sets the name of the Person.")
    private String name;

    @DataClassBuilder.MethodDocs(text = "This method sets the surname of the Person.")
    private String surname;

    @DataClassBuilder.MethodDocs(text = "This method sets the address where the person lives.")
    private Address address;

    Person(int age, String name, String surname, Address address) {
        this.age = age;
        this.name = name;
        this.surname = surname;
        this.address = address;
    }
}
