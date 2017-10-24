package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

/**
 * Created by rsulkowski on 24/10/2017.
 */

@Getter
@DataClassBuilder(buildMethodReturnType = INote.class)
public class Note implements INote {

    private int pages;
    private String title;

    Note(int pages, String title) {
        this.pages = pages;
        this.title = title;
    }
}
