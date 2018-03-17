package org.sunricher.wifi.mqtt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.sunricher.wifi.api.Constants;

public class UPDClient {
	private String host;
	private InetAddress inetAddress = null;
	private DatagramSocket ds = null;
	private MqttClient mqttClient;
	private String mqttPublishTopic;

	public UPDClient(String aHost) {
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
			System.out.println("Could not connect to UDP");
			e.printStackTrace();
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
		System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(sendBytes));
		DatagramPacket datagram = new DatagramPacket(sendBytes, sendBytes.length, getInetAddress(), Constants.UDP_PORT);
		try {
			ds.send(datagram);
		} catch (IOException e) {
			System.out.println("Could not send data via UDP");
			e.printStackTrace();
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

		message.setPayload(null);
		try {
			this.mqttClient.publish(this.mqttPublishTopic, message);
		} catch (MqttPersistenceException e) {
			System.out.println("Could not send MQTT message");
			e.printStackTrace();
		} catch (MqttException e) {
			System.out.println("Could not send MQTT message");
			e.printStackTrace();
		}
	}

	public void setMqttPublishTopic(String string) {
		this.mqttPublishTopic = string;
	}
}