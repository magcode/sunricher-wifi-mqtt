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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.config.Configurator;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.magcode.sunricher.api.Constants;
import org.magcode.sunricher.api.WifiHandler;
import org.magcode.sunricher.api.WifiHandlerImpl;

public class SunricherClient {
	private static String mqttServer;
    private static String mqttUsername;
    private static String mqttPassword;
    private static int mqttVersion;
	private static String ledControllerHost;
	private static int repeat;
	private static String topic;
	private static TcpClient tcpClient;
	private static MqttClient mqttClient;
	private static WifiHandler ledHandler;
	private static UDPClient udpClient;
	private static boolean disableWifi = false;
	private static String logLevel;

	private static Logger logger = LogManager.getLogger(SunricherClient.class);

	public static void main(String[] args) throws Exception {
		readProps();
		reConfigureLogger();
		// updClient handles AT commands for HF-11A module
		udpClient = new UDPClient(ledControllerHost);
		udpClient.init();
		udpClient.setDisableWifi(disableWifi);

		// tcpClient handles LED control messages
		tcpClient = new TcpClient(ledControllerHost, Constants.TCP_PORT);
		tcpClient.init();
		tcpClient.setUpdClient(udpClient);

		ledHandler = new WifiHandlerImpl(tcpClient, repeat);
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
            mqttUsername = props.getProperty("mqttUsername");
            mqttPassword = props.getProperty("mqttPassword");
            mqttVersion = new Integer(props.getProperty("mqttVersion", "3"));
			repeat = new Integer(props.getProperty("repeat", "4"));
			topic = props.getProperty("topic", "home/led");
			ledControllerHost = props.getProperty("ledHost", "192.168.0.1");
			disableWifi = Boolean.parseBoolean(props.getProperty("disableWifi", "false"));
			logLevel = props.getProperty("logLevel", "INFO");
		} catch (IOException ex) {
			logger.error("Cannot read properties", ex);
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
        connOpt.setMqttVersion(mqttVersion);
        if (mqttUsername != null && mqttPassword != null) {
            connOpt.setUserName(mqttUsername);
            connOpt.setPassword(mqttPassword.toCharArray());
        }
		mqttClient.setCallback(new MqttSubscriber(ledHandler, udpClient));
		mqttClient.connect(connOpt);
		logger.info("Connected to MQTT broker.");
		mqttClient.subscribe(topic + "/+/+");
		udpClient.setMqttClient(mqttClient);
		logger.info("Sending UDP messages to MQTT topic {}", topic + "/received");
		udpClient.setMqttPublishTopic(topic + "/received");
		logger.info("Subscribed to {}", topic);
	}

	/**
	 * Reconfigures log4j2 and changes the filename. that might be helpful when
	 * running multiple instances.
	 */
	private static void reConfigureLogger() {
		org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager
				.getContext(false);
		ctx.reconfigure();
		Configurator.setRootLevel(Level.forName(logLevel, 0));
	}
}