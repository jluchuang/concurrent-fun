package ioc.container;

import ioc.heros.DiauCharn;
import ioc.test.KingOfGlory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.ClassArrayEditor;

/**
 * Created by chuang on 2017/8/21.
 */
public class SampleContainerTest {
    private static Container container;

    @Before
    public void prepare() {
        container = new SampleContainer();
        container.registerBean("diaocharn", new DiauCharn());
        container.registerBean(KingOfGlory.class);
        container.initWired();
    }

    @Test
    public void baseTest() {
        KingOfGlory kingOfGlory = container.getBean(KingOfGlory.class);
        kingOfGlory.play();
    }

}
