package org.magcode.sunricher.mqtt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.sunricher.wifi.api.ColorHandler;
import org.sunricher.wifi.api.ColorHandlerImpl;
import org.sunricher.wifi.api.Constants;

public class SunricherClient {
	private static String mqttServer;
	private static String ledControllerHost;
	private static String topic;
	private static TcpClient tcpClient;
	private static MqttClient mqttClient;
	private static ColorHandler ledHandler;
	private static UDPClient udpClient;
	private static boolean disableWifi = false;

	private static Logger logger = LogManager.getLogger(SunricherClient.class);

	public static void main(String[] args) throws Exception {
		readProps();
		// updClient handles AT commands for HF-11A module
		udpClient = new UDPClient(ledControllerHost);
		udpClient.init();
		udpClient.setDisableWifi(disableWifi);

		// tcpClient handles LED control messages
		tcpClient = new TcpClient(ledControllerHost, Constants.TCP_PORT);
		tcpClient.init();
		tcpClient.setUpdClient(udpClient);

		ledHandler = new ColorHandlerImpl(tcpClient);
		// connect to MQTT broker
		startMQTTClient();

		if (disableWifi) {
			// disable wifi after some seconds
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			@SuppressWarnings("unused")
			ScheduledFuture<?> disabler = executor.schedule(new WifiDisabler(udpClient), 10, TimeUnit.SECONDS);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Logger logger2 = LogManager.getLogger("shutdown");
				try {
					mqttClient.disconnect();
					logger2.info("Disconnected from MQTT server");
					tcpClient.shutDown();
					((LifeCycle) LogManager.getContext()).stop();
				} catch (MqttException e) {
					logger2.error("Error during shutdown", e);
				}
			}
		});
	}

	private static void readProps() {
		Properties props = new Properties();
		InputStream input = null;

		try {
			File jarPath = new File(
					SunricherClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesPath = jarPath.getParentFile().getAbsolutePath();
			String filePath = propertiesPath + "/sunricher.properties";
			logger.info("Loading properties from {}", filePath);

			input = new FileInputStream(filePath);
			props.load(input);
			mqttServer = props.getProperty("mqttServer", "tcp://localhost");
			topic = props.getProperty("topic", "home/led");
			ledControllerHost = props.getProperty("ledHost", "192.168.0.1");
			disableWifi = Boolean.parseBoolean(props.getProperty("disableWifi", "false"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Failed to close file", e);
				}
			}
		}
	}

	private static void startMQTTClient() throws MqttException {
		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error("Failed to get hostname", e);
		}
		mqttClient = new MqttClient(mqttServer, "client-for-led-on-" + hostName);
		MqttConnectOptions connOpt = new MqttConnectOptions();
		connOpt.setCleanSession(false);
		connOpt.setKeepAliveInterval(30);
		connOpt.setAutomaticReconnect(true);
		mqttClient.setCallback(new MqttSubscriber(ledHandler, udpClient));
		mqttClient.connect();
		logger.info("Connected to MQTT broker.");
		mqttClient.subscribe(topic + "/+/+");
		udpClient.setMqttClient(mqttClient);
		logger.info("Sending UDP messages to MQTT topic {}", topic + "/received");
		udpClient.setMqttPublishTopic(topic + "/received");
		logger.info("Subscribed to {}", topic);
	}
}