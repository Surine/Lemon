package com.surine.lemon.InitApp

import android.app.Application

import com.surine.lemon.Utils.FontUtil

/**
 * Created by surine on 2017/9/1.
 * 初始化Application
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //设置初始字体
        FontUtil.setDefaultFont(this, "SERIF", "fonts/s3.ttf")
    }

}
