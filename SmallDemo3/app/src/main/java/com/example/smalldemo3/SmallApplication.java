package com.example.smalldemo3;

import android.app.Application;

import net.wequick.small.Small;

/**
 * author : yjj
 * date   : 2021/3/1114:10
 * desc   :
 */
public class SmallApplication extends Application {

    public SmallApplication() {
        // content provider在onCreate之前调用(android源码)，为了让Small支持CP，在这边进行Small的preSetUp
        Small.preSetUp(this);
    }

}
