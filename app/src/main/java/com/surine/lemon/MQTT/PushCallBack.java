package com.surine.lemon.MQTT;

import android.util.Log;

import com.surine.lemon.JavaBean.SimpleEvent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import de.greenrobot.event.EventBus;

public class PushCallBack implements MqttCallback {

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
        Log.d("MQTT_MESSAGE", "链接断开");
		EventBus.getDefault().post(
				new SimpleEvent(2,"OFF"));
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
        Log.d("MQTT_MESSAGE", "deliveryComplete----------"+arg0.isComplete());
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		Log.d("MQTT_MESSAGE", "接受消息内容"+new String(arg1.getPayload()));
		Log.d("MQTT_MESSAGE", "接收消息主题"+arg0);
		//发送全部数据
		if(new String(arg1.getPayload()).contains("lou")||new String(arg1.getPayload()).contains("wangkai19"))
		{
			//该死的楼贤拓
		}else if(new String(arg1.getPayload()).contains("Fire")){
			//发送数据
			EventBus.getDefault().post(
					new SimpleEvent(3,new String(arg1.getPayload())));
		}
		else{
			//发送数据
			EventBus.getDefault().post(
					new SimpleEvent(1,new String(arg1.getPayload())));
		}
	}

	
}
