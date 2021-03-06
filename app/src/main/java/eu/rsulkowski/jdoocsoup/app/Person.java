/**
 * Copyrights.
 */
package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

/**
 * Created by rsulkowski on 02/10/2017.
 */
@Getter
@DataClassBuilder(
        jdocs = "POJO class which represents the Person.",
        builderMethodJDocs = "Creates the new builder object for Person\n@param name it is the name of the person.\n@param surname it is the surname of the person",
        buildMethodJDocs = "Gather all passed information from PersonBuilder and creates new Person object",
        setterPrefix = "with")
public class Person {

    @DataClassBuilder.MethodDocs("This method sets the age of the person. Normally from 0 to 130.")
    @DataClassBuilder.HasDefault("30")
    private int age;

    @DataClassBuilder.MethodDocs("This method sets the name of the Person.")
    @DataClassBuilder.HasDefault("\"John\"")
    @DataClassBuilder.Required
    private String name;

    @DataClassBuilder.MethodDocs("This method sets the surname of the Person.")
    @DataClassBuilder.Required
    private String surname;

    @DataClassBuilder.MethodDocs("This method sets the address where the person lives.")
    private Address address;

    private String someLongStringAddress;

    // Final fields shouldn't be added to builder
    private final int someFinalField = 2;

    // Static fields shouldn't be added to builder
    private static int someStaticField = 3;

    // Final static fields shouldn't be added to builder
    private final static int ANOTHER_FINAL_STATIC_FIELD = 4;

    Person(int age, String name, String surname, Address address, String someLongStringAddress) {
        this.age = age;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.someLongStringAddress = someLongStringAddress;
    }
}
