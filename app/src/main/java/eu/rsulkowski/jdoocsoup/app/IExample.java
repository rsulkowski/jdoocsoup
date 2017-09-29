package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.BuilderInterface;
import eu.rsulkowski.jdoocsoup.annotation.Getter;
import eu.rsulkowski.jdoocsoup.annotation.Setter;

/**
 * Created by rsulkowski on 9/27/17.
 */

@BuilderInterface
public interface IExample {

    /**
     * Id of the Example.
     */
    @Setter(jdocs = "Sets the id of the Example.")
    @Getter(jdocs = "Obtains the id of the Example", jdocsReturn = "id - the id of the Example")
    int id();

    /**
     *
     */
    String name();

    /**
     *
     */
    long timestamp();

}
