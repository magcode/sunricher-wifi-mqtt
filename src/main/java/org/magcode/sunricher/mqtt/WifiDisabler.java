package org.magcode.sunricher.mqtt;

public class WifiDisabler implements Runnable {

	UDPClient udpClient;

	public WifiDisabler(UDPClient udpClient) {
		this.udpClient = udpClient;
	}

	@Override
	public void run() {
		udpClient.sendDisableWifi();
	}
}
