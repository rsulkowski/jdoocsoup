package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.Builder;

/**
 * Created by rsulkowski on 9/27/17.
 */

@Builder
public class Example {

    /**
     * Id of the Example.
     */
    @Builder.Setter(description = "Sets the id of the Example.")
    int id;

    /**
     *
     */
    String name;

    /**
     *
     */
    long timestamp;

}
