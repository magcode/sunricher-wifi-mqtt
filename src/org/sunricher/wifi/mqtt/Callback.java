package org.sunricher.wifi.mqtt;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
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
		// .../1/power
		String[] tops = topic.split("/");
		String channel = tops[tops.length - 2];
		if (!StringUtils.isNumeric(channel) || !StringUtils.isNumeric(message.toString())) {
			return;
		}
		int chan = new Integer(channel);
		int value = new Integer(message.toString());
		if (tops[tops.length - 1].endsWith(BRIGHT)) {
			ArrayList<Integer> zones = new ArrayList<Integer>();
			zones.add(chan);
			ledHandler.setBrightness(zones, value);
		}

		System.out.println(topic);
		System.out.println(message.toString());
	}

	public void deliveryComplete(IMqttDeliveryToken t) {
	}

}