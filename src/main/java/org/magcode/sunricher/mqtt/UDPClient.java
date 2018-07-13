package org.magcode.sunricher.mqtt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.magcode.sunricher.api.Constants;

public class UDPClient {
	private String host;
	private InetAddress inetAddress = null;
	private DatagramSocket ds = null;
	private MqttClient mqttClient;
	private String mqttPublishTopic;
	private boolean disableWifi;
	private static Logger logger = LogManager.getLogger(UDPClient.class);

	public UDPClient(String aHost) {
		this.host = aHost;
	}

	public void init() {
		try {
			String network_address = StringUtils.substringBeforeLast(this.host, ".") + ".255";
			inetAddress = InetAddress.getByName(network_address);
			setDs(new DatagramSocket(48899));

			Executor executor = Executors.newSingleThreadExecutor();
			executor.execute(new UDPListener(this));

		} catch (SocketException | UnknownHostException e) {
			logger.error("Could not connect to UDP", e);
		}
	}

	public DatagramSocket getDs() {
		return ds;
	}

	private void setDs(DatagramSocket ds) {
		this.ds = ds;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public void send(String data) {
		if (!data.equals(Constants.AT_ASSIST) && !data.equals(Constants.AT_OK)) {
			data = data + "\n";
		}
		byte[] sendBytes = data.getBytes();
		logger.info(new String(sendBytes));
		DatagramPacket datagram = new DatagramPacket(sendBytes, sendBytes.length, getInetAddress(), Constants.UDP_PORT);
		try {
			ds.send(datagram);
		} catch (IOException e) {
			logger.error("Could not send data via UDP", e);
		}
	}

	public void sendDisableWifi() {
		if (this.isDisableWifi()) {
			try {
				logger.info("Disabling Wifi...");
				send("HF-A11ASSISTHREAD");
				Thread.sleep(1000);
				send("+ok");
				Thread.sleep(1000);
				send("AT+MSLP=off");
			} catch (InterruptedException e) {
				logger.error("Interrupted", e);
			}
		}
	}

	public void setMqttClient(MqttClient someMQTTClient) {
		this.mqttClient = someMQTTClient;
	}

	public void sendMqttMessage(byte[] bytes) {
		if (this.mqttClient == null || StringUtils.isBlank(this.mqttPublishTopic)) {
			return;
		}
		MqttMessage message = new MqttMessage(bytes);

		try {
			this.mqttClient.publish(this.mqttPublishTopic, message);
		} catch (MqttException e) {
			logger.error("Could not send MQTT message", e);
		}
	}

	public void setMqttPublishTopic(String string) {
		this.mqttPublishTopic = string;
	}

	public boolean isDisableWifi() {
		return disableWifi;
	}

	public void setDisableWifi(boolean disableWifi) {
		this.disableWifi = disableWifi;
	}
}