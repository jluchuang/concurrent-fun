package ioc.test;

import ioc.annotation.SampleAutowired;

import ioc.heros.Arthur;
import ioc.heros.DiauCharn;

/**
 * Created by chuang on 2017/8/21.
 */
public class KingOfGlory {
    @SampleAutowired
    Arthur arthur;

    @SampleAutowired(value = DiauCharn.class)
    DiauCharn diauCharn;

    @SampleAutowired(name = "diaocharn",value = DiauCharn.class)
    DiauCharn diauCharn1;


    public void play() {
        arthur.pledgeShield();
        diauCharn.talant();
        diauCharn1.talant();
    }

}
