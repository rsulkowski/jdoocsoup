package eu.rsulkowski.jdoocsoup.app;

import java.io.Serializable;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

/**
 * Created by rsulkowski on 2017-10-23.
 */
@Getter
@DataClassBuilder(jdocs = "The type of the Fruit which has a problem with worms.",
        builderMethodJDocs = "Creates the new builder object for Apple",
        buildMethodJDocs = "Gathers all passed information from AppleBuilder and the base class and creates new Apple object",
        implementInterfaces = {Colorful.class, Serializable.class}
)
public class Apple extends Fruit {

    @DataClassBuilder.MethodDocs("Some javadocs for wormName.\n@param wormName - the name of the worm inside the apple\n@return the builder.")
    private String wormName;

    Apple(String wormName, Integer weight, String color,String sizeKg) {
        this.wormName = wormName;
        this.weight = weight;
        this.color = color;
        this.sizeKg = sizeKg;
    }
}
