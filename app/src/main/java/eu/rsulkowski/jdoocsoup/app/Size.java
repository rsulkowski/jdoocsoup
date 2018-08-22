package eu.rsulkowski.jdoocsoup.app;

import eu.rsulkowski.jdoocsoup.annotation.DataClassBuilder;
import lombok.Getter;

@Getter
abstract class Size {

    protected String sizeKg;

    @DataClassBuilder.Ignored
    protected String sizeMgr;
}
