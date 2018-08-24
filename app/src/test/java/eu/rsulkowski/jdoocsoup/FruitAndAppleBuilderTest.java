package eu.rsulkowski.jdoocsoup;

import junit.framework.Assert;

import org.junit.Test;

import eu.rsulkowski.jdoocsoup.app.Apple;
import eu.rsulkowski.jdoocsoup.app.AppleBuilder;

/**
 * Created by rsulkowski on 2017-10-23.
 */

public class FruitAndAppleBuilderTest {

    @Test
    public void createAppleByBuilder() {
        Apple apple = AppleBuilder.create("Teddy")
                .withColor("Green")
                .withWeight(2).withPoint("fdsfds")
                .build();
        apple.getSizeKg();
        Assert.assertEquals("Teddy", apple.getWormName());
        Assert.assertEquals("Green", apple.getColor());


    }

}
