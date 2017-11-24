package com.surine.lemon.MQTT

import android.util.Log
import com.surine.lemon.JavaBean.SimpleEvent
import de.greenrobot.event.EventBus
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Created by surine on 2017/9/20.
 * MQTT回调
 */

class PushCallBack : MqttCallback {

    override fun connectionLost(arg0: Throwable) {
        EventBus.getDefault().post(SimpleEvent(2, "OFF"))
    }

    override fun deliveryComplete(arg0: IMqttDeliveryToken) {

    }

    @Throws(Exception::class)
    override fun messageArrived(arg0: String, arg1: MqttMessage) {
        Log.d("EEE", arg1.toString())
        //信息处理发送
        when{
            String(arg1.payload).contains("lou") or  String(arg1.payload).contains("wangkai19") -> Log.d("E","NULL")
            String(arg1.payload).contains("Fire") -> EventBus.getDefault().post(SimpleEvent(3, String(arg1.payload)))
            String(arg1.payload).contains("FLAG") ->
            {
                EventBus.getDefault().post(SimpleEvent(4, String(arg1.payload)))
                Log.d("EEE", "数据已发送")
            }
            else -> EventBus.getDefault().post(SimpleEvent(1, String(arg1.payload)))
        }
    }
}
