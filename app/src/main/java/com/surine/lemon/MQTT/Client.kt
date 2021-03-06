package com.surine.lemon.MQTT

import com.surine.lemon.Data.UrlData
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

/**
 * Created by surine on 2017/9/20.
 * MQTT连接
 */

class Client {
    private var client: MqttClient? = null
    private var options: MqttConnectOptions? = null


    //客户端接收
    fun start() {
        val clientid = android.os.Build.SERIAL
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，
            // 一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = MqttClient(UrlData.host, clientid, MemoryPersistence())
            // MQTT的连接设置
            options = MqttConnectOptions()
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options!!.isCleanSession = true
            // 设置超时时间 单位为秒
            options!!.connectionTimeout = 10
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options!!.keepAliveInterval = 20
            // 设置回调
            client!!.setCallback(PushCallBack())
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            client!!.connect(options)
            //订阅消息
            client!!.subscribe(UrlData.outTopic, 1)

        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    //客户端发送
    fun sendCmd(s: String) {
        //Publish方法
        val b = s.toByteArray()
        try {
            client!!.publish(UrlData.inTopic, b, 0, false)
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }
}
