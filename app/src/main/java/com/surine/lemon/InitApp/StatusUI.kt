package com.surine.lemon.InitApp

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View

/**
 * Created by surine on 2017/8/3.
 * 配置状态栏UI
 */

object StatusUI {
    fun StatusUISetting(context: Activity, color: String) {

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = context.window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            context.window.statusBarColor = Color.parseColor(color)
        }
    }
}
