package org.sunricher.wifi.mqtt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.sunricher.wifi.api.DeviceHandler;
import org.sunricher.wifi.api.DeviceHandlerImpl;

public class Client {
	private static String mqttServer;
	private static String ledControllerHost;
	private static int ledControllerPort;
	private static String topic;
	private static DeviceHandler device = null;
	private static MqttClient mqttClient;
	private static boolean connecctionInProgress = false;
	public static OutputStream sessionOutputStream = null;

	public static void main(String[] args) throws Exception {
		if (StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1]) || StringUtils.isBlank(args[2])
				|| StringUtils.isBlank(args[3]) || !StringUtils.isNumeric(args[3])) {
			System.out.println("Missing arguments");
			return;
		}

		mqttServer = args[0];
		topic = args[1];
		ledControllerHost = args[2];
		ledControllerPort = new Integer(args[3]);

		startMQTTClient();
		device = new DeviceHandlerImpl();
		connectToController(false);

		// add handle for ctrl+c to disconnect
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					System.out.println("Disconnecting from server");
					device.disconnect();
					mqttClient.disconnect();
				} catch (IOException | MqttException e) {
					// we cannot do much here.
					e.printStackTrace();
				}
			}
		});
	}

	private static void startMQTTClient() throws MqttException {
		System.out.println("Starting MQTT Client ...");
		mqttClient = new MqttClient(mqttServer, generateClientId());
		mqttClient.setCallback(new Callback());
		mqttClient.connect();
		mqttClient.subscribe(topic);
		System.out.println("Connected and subscribed to " + topic);
	}

	private static String generateClientId() {
		return UUID.randomUUID().toString();
	}

	public static void connectToController(boolean reconnect) {
		// if a thread was starting the reconnection progress we can stop right
		// here
		if (connecctionInProgress) {
			System.out.println(
					"Reconnecting to LED Controller will be ommitted because another process is trying to connect.");
			return;
		}

		connecctionInProgress = true;
		if (reconnect) {
			System.out.println("Reconnecting to LED Controller");
			try {
				device.disconnect();
			} catch (Exception e) {
				System.out.println("error while trying to disconnect from device. continuing anyway");
				e.printStackTrace();
			}
		} else {
			System.out.println("Connecting to LED Controller");
		}

		boolean reconnected = false;

		while (reconnected == false) {
			try {
				sessionOutputStream = device.connect(ledControllerHost, ledControllerPort);
				reconnected = true;
				System.out.println("Connection established.");
				connecctionInProgress = false;
			} catch (Exception e) {
				System.out.println("(Re)connection failed. Waiting for 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// we cannot do much here.
				}
			}
		}
	}
}
