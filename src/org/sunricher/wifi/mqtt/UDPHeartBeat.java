package org.sunricher.wifi.mqtt;

import java.io.IOException;
import java.net.DatagramPacket;

import org.sunricher.wifi.api.Constant;

public class UDPHeartBeat implements Runnable {
	private UPDClient client;

	public UDPHeartBeat(UPDClient aClient) {
		this.client = aClient;

	}

	@Override
	public void run() {

		try {
			byte[] send_data = new byte[1];
			DatagramPacket datagram = new DatagramPacket(send_data, send_data.length, client.getInetAddress(),
					Constant.UDP_DATA_SEND_PORT);
			client.getDs().send(datagram);
			System.out.println("Sent " + send_data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
