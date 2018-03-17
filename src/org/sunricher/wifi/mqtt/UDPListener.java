package org.sunricher.wifi.mqtt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPListener implements Runnable {
	private UPDClient client;
	private byte[] receive_str_byte = new byte[2048];

	public UDPListener(UPDClient aClient) {
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

				System.out.printf("Request from %s Port %d:%n%s%n", address, port,
						new String(data, 0, len));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}