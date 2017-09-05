package com.surine.lemon.Data;

/**
 * Created by surine on 2017/5/20.
 */

public class UrlData {


    /***********************
     * MQTT配置项
     * *************************/
    public static String url = "暂无数据";
    public static String host = "tcp://louxiantuo.xin:1883";   //ip
    public static String Hum = "Hum";   //湿度
    public static String Tem = "Tem";   //温度
    public static String LPG = "LPG";   //甲烷
    public static String CAR = "CAR";   //CO
    public static String MET = "MET";   //乙烷
    public static String open = "ope";   //开窗
    public static String close = "clo";   //关窗

    public static String inTopic = "inTopic";   //发送
    public static String outTopic = "outTopic";   //接受

    /***********************
     * 图标配置项
     * *************************/
    public static float Y_MIN = -20;   //y 最小值
    public static float Y_MAX = 40;   //y 最大值
    public static float HEART_NUMBER_SHOW = 12;   //更新范围
}
