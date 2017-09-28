package eu.rsulkowski.jdoocsoup.app;

import java.util.Date;

/**
 * Created by rsulkowski on 9/28/17.
 */

public class AppExample {

    public void someExampleMethod() {
        Example example = ExampleBuilder.builder()
                .id(1)
                .name("exampleName")
                .timestamp(new Date().getTime())
                .build();
    }
}
