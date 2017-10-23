package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

/**
 * Created by rsulkowski on 10/3/17.
 */
@Getter
@DataClassBuilder(
        jdocs = "POJO class which represents the Address.",
        builderMethodJDocs = "Creates the new builder object for Address",
        buildMethodJDocs = "Gather all passed information from AddressBuilder and creates new Address object")
public class Address {

    @DataClassBuilder.MethodDocs("This method sets on the builder the country in example: Poland")
    private String country;

    @DataClassBuilder.MethodDocs("This method sets on the builder the city name like: Warsaw")
    private String city;

    @DataClassBuilder.MethodDocs("This method sets on the builder the street name like: Dolna")
    private String street;

    @DataClassBuilder.MethodDocs("This method sets on the builder the street number like: 20")
    private int streetNumber;

    Address(String country, String city, String street, int streetNumber) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
    }
}