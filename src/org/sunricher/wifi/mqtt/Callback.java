package org.sunricher.wifi.mqtt;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.sunricher.wifi.api.ColorHandler;

public class Callback implements MqttCallback {
	private static final String BRIGHT = "brightness";
	private static final String POW = "power";
	private static ColorHandler ledHandler;

	public Callback(ColorHandler aLedHandler) {
		ledHandler = aLedHandler;
	}

	public void connectionLost(Throwable throwable) {
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// so far implemented:
		// .../1/brightness
		// .../1,2/power payload "0" or "1"

		String[] tops = topic.split("/");
		String channel = tops[tops.length - 2];

		ArrayList<Integer> zonesA = new ArrayList<Integer>();
		String[] zones = channel.toString().split(",");
		for (String aZone : zones) {
			int value = new Integer(aZone);
			zonesA.add(value);
		}
		int value = new Integer(message.toString());

		switch (tops[tops.length - 1]) {
		case BRIGHT:
			ledHandler.setBrightness(zonesA, value);
		case POW:
			ledHandler.togglePower(zonesA, value == 1);
		}

		System.out.println("topic:" + topic + " message:" + message.toString());
	}

	public void deliveryComplete(IMqttDeliveryToken t) {
	}

}