package org.magcode.sunricher.mqtt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPListener implements Runnable {
	private UDPClient client;
	private byte[] receive_str_byte = new byte[2048];
	private static Logger logger = LogManager.getLogger(UDPListener.class);

	public UDPListener(UDPClient aClient) {
		this.client = aClient;
	}

	@Override
	public void run() {
		while (true) {
			DatagramPacket packet = new DatagramPacket(receive_str_byte, receive_str_byte.length);
			try {
				client.getDs().receive(packet);
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				int len = packet.getLength();
				byte[] data = packet.getData();
				client.sendMqttMessage(data);
				logger.info("Request from {} {}: {}", address, port, new String(data, 0, len));
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}