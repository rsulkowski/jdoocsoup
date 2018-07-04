package eu.rsulkowski.jdoocsoup;

import junit.framework.Assert;

import org.junit.Test;

import eu.rsulkowski.jdoocsoup.app.Address;
import eu.rsulkowski.jdoocsoup.app.AddressBuilder;
import eu.rsulkowski.jdoocsoup.app.Person;
import eu.rsulkowski.jdoocsoup.app.PersonBuilder;

import static org.junit.Assert.assertEquals;

public class PersonAndAddressBuilderTest {

    @Test
    public void createAddressByBuilder() {
        Address address = AddressBuilder
                .create()
                .country("Poland")
                .city("Warsaw")
                .street("Dolna")
                .streetNumber(20)
                .build();

        Assert.assertEquals("Poland", address.getCountry());
        Assert.assertEquals("Warsaw", address.getCity());
        Assert.assertEquals("Dolna", address.getStreet());
        Assert.assertEquals(20, address.getStreetNumber());
    }

    @Test
    public void createPersonByBuilder() {
        Address address = AddressBuilder
                .create()
                .country("Poland")
                .city("Warsaw")
                .street("Dolna")
                .streetNumber(20)
                .build();

        Person person = PersonBuilder.create("Jane", "Surname")
                .address(address)
                .build();

        Assert.assertEquals("Poland", person.getAddress().getCountry());
        Assert.assertEquals("Warsaw", person.getAddress().getCity());
        Assert.assertEquals("Dolna", person.getAddress().getStreet());
        Assert.assertEquals(20, person.getAddress().getStreetNumber());
        Assert.assertEquals("Jane", person.getName());
        Assert.assertEquals("Surname", person.getSurname());
        Assert.assertEquals(30, person.getAge());
    }
}