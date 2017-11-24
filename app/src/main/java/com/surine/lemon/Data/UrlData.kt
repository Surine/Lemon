package com.surine.lemon.Data

/**
 * Created by surine on 2017/10/1.
 */

object UrlData {


    /***********************
     * MQTT配置项
     ***********************/
    var url = "暂无数据"
    var host = "tcp://louxiantuo.xin:1883"   //ip
    var Hum = "Hum"   //湿度
    var Tem = "Tem"   //温度
    var LPG = "LPG"   //甲烷
    var CAR = "CAR"   //CO
    var MET = "MET"   //乙烷
    var open = "ope"   //开窗
    var close = "clo"   //关窗
    var inTopic = "inTopic"   //发送
    var outTopic = "outTopic"   //接受
    var co_max = 15


    /***********************
     * 图表配置项
     ***********************/
    var Y_MIN = -20f   //y 最小值
    var Y_MAX = 40f   //y 最大值
    var HEART_NUMBER_SHOW = 12f   //更新范围


    /*************************
     * API配置
     * Bing每日图片及一句API
     ***********************/
    var bing_picture = "http://api.dujin.org/bing/1920.php"
    var title_ = "http://yiju.ml/api/word.php"
}
