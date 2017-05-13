package org.sunricher.wifi.mqtt;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.sunricher.wifi.api.ColorHandler;
import org.sunricher.wifi.api.ColorHandlerImpl;

public class Client {
	private static String mqttServer;
	private static String ledControllerHost;
	private static int ledControllerPort;
	private static String topic;
	private static TcpClient tcpClient;
	private static MqttClient mqttClient;
	private static ColorHandler ledHandler;
	private static UPDClient udpClient;

	public static void main(String[] args) throws Exception {
		if (StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1]) || StringUtils.isBlank(args[2])
				|| StringUtils.isBlank(args[3]) || !StringUtils.isNumeric(args[3])) {
			System.out.println("Missing arguments");
			return;
		}
		// args
		mqttServer = args[0];
		topic = args[1];
		ledControllerHost = args[2];
		ledControllerPort = new Integer(args[3]);

		tcpClient = new TcpClient(ledControllerHost, ledControllerPort);
		tcpClient.init();

		udpClient = new UPDClient(ledControllerHost);
		udpClient.init();

		ledHandler = new ColorHandlerImpl(tcpClient);
		// connect to MQTT broker
		startMQTTClient();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					mqttClient.disconnect();
					System.out.println("Disconnected from MQTT server");
					tcpClient.shutDown();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void startMQTTClient() throws MqttException {
		System.out.println("Starting MQTT Client ...");
		mqttClient = new MqttClient(mqttServer, "Client-for-led-"+ledControllerHost);
		mqttClient.setCallback(new Callback(ledHandler, udpClient));
		mqttClient.connect();
		mqttClient.subscribe(topic + "/+/+");
		System.out.println("Connected and subscribed to " + topic);
	}
}