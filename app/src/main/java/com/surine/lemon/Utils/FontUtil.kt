package com.surine.lemon.Utils

import android.content.Context
import android.graphics.Typeface

/**
 * Created by surine on 2017/9/1.
 * 字体管理工具
 */

object FontUtil {
    fun setDefaultFont(context: Context,
                       staticTypefaceFieldName: String, fontAssetName: String) {
        val regular = Typeface.createFromAsset(context.assets,
                fontAssetName)
        replaceFont(staticTypefaceFieldName, regular)
    }

    fun replaceFont(staticTypefaceFieldName: String,
                              newTypeface: Typeface) {
        try {
            val staticField = Typeface::class.java!!
                    .getDeclaredField(staticTypefaceFieldName)
            staticField.setAccessible(true)
            staticField.set(null, newTypeface)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }
}
