package com.surine.lemon.Utils

import java.util.*
import java.util.regex.Pattern

/**
 * Created by surine on 2017/9/2.
 * 正则规则
 */

object PatternUtil {


    /********************
     * 提取字符串里的数字，并且转换为List表返回
     ********************/
    fun getNumber(a: String): List<String> {
        val m = Pattern.compile("\\d+").matcher(a)
        val list = ArrayList<String>()
        while (m.find()) {
            list.add(m.group())
        }
        return list
    }

}
