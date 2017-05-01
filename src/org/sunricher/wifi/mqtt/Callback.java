package org.sunricher.wifi.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Callback implements MqttCallback {

	public void connectionLost(Throwable throwable) {
	}

	public void messageArrived(String t, MqttMessage m) throws Exception {
		String message = m.toString().replaceAll("#", "");
		System.out.println(message);
	}

	public void deliveryComplete(IMqttDeliveryToken t) {
	}

}