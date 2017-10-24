package eu.rsulkowski.jdoocsoup;

import org.junit.Assert;
import org.junit.Test;

import eu.rsulkowski.jdoocsoup.app.INote;
import eu.rsulkowski.jdoocsoup.app.NoteBuilder;

/**
 * Created by rsulkowski on 24/10/2017.
 */

public class NoteBuildMethodTest {

    @Test
    public void testIfPossibleToReturnInterfaceInsteadOfActualClass(){
        INote note = NoteBuilder.create()
                .pages(2)
                .title("Some note")
                .build();

        Assert.assertNotNull(note);
    }

}
