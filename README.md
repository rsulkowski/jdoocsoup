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
```

Generated Builder:

```java
/**
 * POJO class which represents the Person.
 */
public class PersonBuilder {
  private int age = 30;

  private String name = "John";

  private String surname;

  private Address address;

  private String someLongStringAddress;

  private PersonBuilder(String name, String surname) {
    this.name=name;
    this.surname=surname;
  }

  /**
   * This method sets the age of the person. Normally from 0 to 130.
   */
  public PersonBuilder withAge(int age) {
    this.age = age;
    return this;
  }

  /**
   * This method sets the address where the person lives.
   */
  public PersonBuilder withAddress(Address address) {
    this.address = address;
    return this;
  }

  public PersonBuilder withSomeLongStringAddress(String someLongStringAddress) {
    this.someLongStringAddress = someLongStringAddress;
    return this;
  }

  /**
   * Creates the new builder object for Person
   * @param name it is the name of the person.
   * @param surname it is the surname of the person
   */
  public static PersonBuilder create(String name, String surname) {
    return new PersonBuilder(name,surname);
  }

  /**
   * Gather all passed information from PersonBuilder and creates new Person object
   */
  public Person build() {
    return new Person(age,name,surname,address,someLongStringAddress);
  }
}
```

## Inheritance support

Starting with the version 0.1.3 of the library it is enabled for the builder to look for fields and methods at the base class.
As an example the simple code bellow:

Base class:
```java
abstract class Fruit {

    @DataClassBuilder.MethodDocs("Some javadocs for weight.")
    protected int weight;

    @DataClassBuilder.MethodDocs("Some javadocs for color.")
    protected String color;
}
```

Subclass:
```java
@DataClassBuilder(jdocs = "The type of the Fruit which has a problem with worms.",
        builderMethodJDocs = "Creates the new builder object for Apple",
        buildMethodJDocs = "Gathers all passed information from AppleBuilder and the base class and creates new Apple object")
public class Apple extends Fruit {

    @DataClassBuilder.MethodDocs("Some javadocs for wormName.\n@param wormName - the name of the worm inside the apple\n@return the builder.")
    private String wormName;

    Apple(String wormName, int weight, String color) {
        this.wormName = wormName;
        this.weight = weight;
        this.color = color;
    }
}
```
Will produce as an result:
```java
/**
 * The type of the Fruit which has a problem with worms.
 */
public class AppleBuilder {
  private String wormName;

  private int weight;

  private String color;

  private AppleBuilder() {
  }

  /**
   * Some javadocs for wormName.
   * @param wormName - the name of the worm inside the apple
   * @return the builder.
   */
  public AppleBuilder wormName(String wormName) {
    this.wormName = wormName;
    return this;
  }

  /**
   * Some javadocs for weight.
   */
  public AppleBuilder weight(int weight) {
    this.weight = weight;
    return this;
  }

  /**
   * Some javadocs for color.
   */
  public AppleBuilder color(String color) {
    this.color = color;
    return this;
  }

  /**
   * Creates the new builder object for Apple
   */
  public static AppleBuilder create() {
    return new AppleBuilder();
  }

  /**
   * Gathers all passed information from AppleBuilder and the base class and creates new Apple object
   */
  public Apple build() {
    return new Apple(wormName,weight,color);
  }
}
```
## Builder build method return type

Since version **0.1.5** It is possible to return different type by the terminal 'build' method
by using buildMethodReturnType param of the @DataClassBuilder annotation.

Data class:

```java
@DataClassBuilder(buildMethodReturnType = INote.class)
public class Note implements INote {

    private int pages;
    private String title;

    Note(int pages, String title) {
        this.pages = pages;
        this.title = title;
    }
}
```

Interface implemented by Data class:

```java
public interface INote {
}

```

Generated builder:

```java
/**
 *
 */
public class NoteBuilder {
  private int pages;

  private String title;

  private NoteBuilder() {
  }

  public NoteBuilder pages(int pages) {
    this.pages = pages;
    return this;
  }

  public NoteBuilder title(String title) {
    this.title = title;
    return this;
  }

  /**
   *
   */
  public static NoteBuilder create() {
    return new NoteBuilder();
  }

  /**
   *
   */
  public INote build() {
    return new Note(pages,title);
  }
}

```

## @DataClassBuilder.Ignored annotation

Since the version 0.1.6 it is possible to ignore fields explicitly from being used for generated builder. 
It is done via @DataClassBuilder.Ignored annotation:

```java
@Getter
@DataClassBuilder(jdocs = "The type of the Fruit which has a problem with worms.",
        builderMethodJDocs = "Creates the new builder object for Apple",
        buildMethodJDocs = "Gathers all passed information from AppleBuilder and the base class and creates new Apple object")
public class Apple extends Fruit {

    @DataClassBuilder.MethodDocs("Some javadocs for wormName.\n@param wormName - the name of the worm inside the apple\n@return the builder.")
    private String wormName;

    @DataClassBuilder.Ignored
    private String tagToBeIgnored;

    Apple(String wormName, int weight, String color) {
        this.wormName = wormName;
        this.weight = weight;
        this.color = color;
    }
}

```

## @DataClassBuilder.Required annotation

Since the version 0.1.7 it is possible to mark fields in the Data Class as required. If such field will be marked as required,
the method to create(..) the builder will also force setup of those fields:

Specifying fields like this in Data Class:

```java
    @DataClassBuilder.Required
    private String name;

    @DataClassBuilder.Required
    private String surname;
```

Will result with this in Data Class's Builder:

```java
  public static PersonBuilder create(String name, String surname) {
    return new PersonBuilder(name,surname);
  }
```

## @DataClassBuilder optional params
### Since 0.1.8: setterPrefix
It allows to add the prefix to the name of the generated setter methods of Data Class Builder:

*In example:*

When you will use:

```java
@DataClassBuilder(setterPrefix = "with")
```

The result for 'someLongStringAddress' field of Person will be:

```java
  public PersonBuilder withSomeLongStringAddress(String someLongStringAddress) {
    this.someLongStringAddress = someLongStringAddress;
    return this;
  }
```


## Configuration

To start you have to do following steps:

1. Add repository to your root build.gradle:

```groovy
buildscript {

    repositories {
        //...
    }
    dependencies {
        //...
    }
}

allprojects {
    repositories {
        // ..
        jcenter() // latest published version is available on jcenter
    }
}
```

2. Add dependencies to your project's build.gradle
```groovy
dependencies {
    // ..
    implementation 'eu.rsulkowski:jdoocsoup:0.1.10'
    annotationProcessor 'eu.rsulkowski:jdoocsoup:0.1.10'
    testAnnotationProcessor 'eu.rsulkowski:jdoocsoup:0.1.10'
    //..
}
```