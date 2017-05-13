package org.sunricher.wifi.mqtt;

import java.io.IOException;

public class TcpHeartBeat implements Runnable {
	private TcpClient client;

	public TcpHeartBeat(TcpClient aClient) {
		this.client = aClient;
	}

	@Override
	public void run() {

		try {
			byte[] send_data = new byte[1];
			this.client.getOs().write(send_data);
			this.client.getOs().flush();
			System.out.println("Tcp keep alive");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No connection");
			client.connect();
		}
	}
}