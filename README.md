# JDoocSoup

## About
The JDoocSoup library primary goal is to minimize the boilerplate code by the generation of the often used classes and methods like Builder for POJO and   
to provide the advanced control over the javadocs at generated classes. Of course there is a great alternative library Lombok but it has no extended support for Javadocs.
This problem is especially visible with @Builder annotation and this library wants to fill this hole.

## JDoocSoup vs Lombok
### Pros
+ No need for additional plugin added to the IDE.
+ It actually generates classes so it is easy to see source code of what was created.
+ Extended control over the javadocs on generated Builder class.
+ Uses newest annotation processing techniques.

### Cons
+ Not yet so mature.
+ Currently only two annotations are supported: @DataClassBuilder and @DataClassBuilder.MethodDocs (others are planned).
+ It won't change the annotated classes so it is not possible to just 'insert' methods / fields / inner classes. It will always require to create new classes.

### Not Pros or Cons
+ It is possible to use Lombok at the same time (with one exclusion: @Builder annotation)
+ Due to the fact that the way how it works differs from the Lombok way, it sometimes forces to add more metadata to annotated classes.

## Generation results:

Example input POJO class:

```java
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
```

Generated Builder:

```java
/**
 * POJO class which represents the Person.
 */
public class PersonBuilder {
  private int age;

  private String name;

  private String surname;

  private Address address;

  private PersonBuilder() {
  }

  /**
   * This method sets the age of the person. Normally from 0 to 130.
   */
  public PersonBuilder age(int age) {
    this.age = age;
    return this;
  }

  /**
   * This method sets the name of the Person.
   */
  public PersonBuilder name(String name) {
    this.name = name;
    return this;
  }

  /**
   * This method sets the surname of the Person.
   */
  public PersonBuilder surname(String surname) {
    this.surname = surname;
    return this;
  }

  /**
   * This method sets the address where the person lives.
   */
  public PersonBuilder address(Address address) {
    this.address = address;
    return this;
  }

  /**
   * Creates the new builder object for Person
   */
  public static PersonBuilder builder() {
    return new PersonBuilder();
  }

  /**
   * Gather all passed information from PersonBuilder and creates new Person object
   */
  public Person build() {
    return new Person(age,name,surname,address);
  }
}
```