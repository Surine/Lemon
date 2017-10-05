package com.surine.lemon.InitApp

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * Created by surine on 2017/9/29.
 * BaseActivity:活动管理
 */

open class BaseActivity : AppCompatActivity() {


    //固定字体大小（禁止缩放）
    override fun getResources(): Resources {
        // TODO Auto-generated method stub
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //配置状态栏UI
        StatusUI.StatusUISetting(this, "#50000000")
        //打印活动名
      //  Log.d("BaseActivity", javaClass.getSimpleName())
        //活动管理器添加活动
        ActivityCollector.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }


    //基础活动内部类
    internal object ActivityCollector {
        var activities: MutableList<Activity> = ArrayList()

        fun addActivity(activity: Activity) {
            activities.add(activity)
        }

        fun removeActivity(activity: Activity) {
            activities.remove(activity)
        }

        fun finishAll() {
            for (activity in activities) {
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
        }
    }

}
