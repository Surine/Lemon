package com.surine.lemon.Utils

import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Created by surine on 2017/9/25.
 * 网络请求工具
 */

object HttpUtil {
    //get
    operator fun get(url: String): Call {
        val builder = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)//设置连接超时时间;
        val okHttpClient = builder.build()
        return okHttpClient.newCall(Request.Builder().url(url).build())
    }

    //post
    fun post(url: String, formBody: FormBody): Call {
        val builder = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)//设置连接超时时间;
        val okHttpClient = builder.build()
        return okHttpClient.newCall(Request.Builder().post(formBody).url(url).build())
    }
}
