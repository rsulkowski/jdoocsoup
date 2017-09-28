package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.BuilderInterface;

/**
 * Created by rsulkowski on 9/27/17.
 */

@BuilderInterface
public interface IExample {

    /**
     * Id of the Example.
     */
    @BuilderInterface.Setter(description = "Sets the id of the Example.")
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
