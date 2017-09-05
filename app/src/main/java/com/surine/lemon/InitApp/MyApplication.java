package com.surine.lemon.InitApp;

import android.app.Application;

import com.surine.lemon.Utils.FontUtil;

/**
 * Created by surine on 2017/9/1.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //use the Library to change the fonts
        FontUtil.setDefaultFont(this, "SERIF","fonts/s3.ttf");
    }

}
