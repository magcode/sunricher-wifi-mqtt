package org.magcode.sunricher.mqtt;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.magcode.sunricher.api.WifiHandler;

public class MqttSubscriber implements MqttCallback {
	private static final String BRIGHT = "brightness";
	private static final String POW = "power";
	private static final String RED = "red";
	private static final String BLUE = "blue";
	private static final String RGBPRG = "prg";
	private static final String RGBPRGSPD = "prgspeed";
	private static final String GREEN = "green";
	private static final String WHITE = "white";
	private static final String HSV = "hsv";
	private static final String RGB = "rgb";
	private static final String RGBDIM = "rgbdim";
	private static final String AT = "at";
	private static WifiHandler ledHandler;
	private UDPClient updClient;
	private static Logger logger = LogManager.getLogger(MqttSubscriber.class);

	public MqttSubscriber(WifiHandler aLedHandler) {
		ledHandler = aLedHandler;
	}

	public MqttSubscriber(WifiHandler aLedHandler, UDPClient anUdpClient) {
		ledHandler = aLedHandler;
		updClient = anUdpClient;
	}

	public void connectionLost(Throwable throwable) {
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.debug("Received MQTT message via topic {}: {}", topic, message.toString());
		
		String[] tops = topic.split("/");
		String channel = tops[tops.length - 2];

		ArrayList<Integer> zonesA = new ArrayList<Integer>();
		String[] zones = channel.toString().split(",");
		for (String aZone : zones) {
			int value = new Integer(aZone);
			zonesA.add(value);
		}

		switch (tops[tops.length - 1]) {
		case BRIGHT:
			int value = new Integer(message.toString());
			value = new Double(value * 2.55).intValue();
			ledHandler.setBrightness(zonesA, value);
			break;
		case RGBDIM:
			value = new Integer(message.toString());
			ledHandler.setRGBBrightness(zonesA, value);
			break;
		case RED:
			value = new Integer(message.toString());
			//value = new Double(value * 2.55).intValue();
			ledHandler.setR(zonesA, value);
			break;
		case BLUE:
			value = new Integer(message.toString());
			//value = new Double(value * 2.55).intValue();
			ledHandler.setB(zonesA, value);
			break;
		case GREEN:
			value = new Integer(message.toString());
			//value = new Double(value * 2.55).intValue();
			ledHandler.setG(zonesA, value);
			break;
		case WHITE:
			value = new Integer(message.toString());
			//value = new Double(value * 2.55).intValue();
			ledHandler.setW(zonesA, value);
			break;
		case RGBPRG:
			value = new Integer(message.toString());
			ledHandler.setRGBFadeProgram(zonesA, value);
			break;
		case RGBPRGSPD:
			value = new Integer(message.toString());
			ledHandler.setRGBFadeSpeed(zonesA, value);
			break;
		case HSV:
			String[] hsv = message.toString().split(",");
			if (hsv.length==3) {
				int h = new Integer(hsv[0]);
				int s = new Integer(hsv[1]);
				int v = new Integer(hsv[2]);
				ledHandler.setHSB(zonesA, h, s, v);
			}
			break;
		case RGB:
			String[] rgb = message.toString().split(",");
			if (rgb.length==3) {
				int r = new Integer(rgb[0]);
				int g = new Integer(rgb[1]);
				int b = new Integer(rgb[2]);
				ledHandler.setRGB(zonesA, r, g,b);
			}
			break;
		case POW:
			value = 0;
			if ("ON".equals(message.toString())) {
				value = 1;
			} else if ("OFF".equals(message.toString())) {
				value = 0;
			} else if (StringUtils.isNumeric(message.toString())) {
				value = new Integer(message.toString());
			}
			ledHandler.togglePower(zonesA, value == 1);
			break;
		case AT:
			updClient.send(message.toString());
			break;
		}
	}

	public void deliveryComplete(IMqttDeliveryToken t) {
	}
}