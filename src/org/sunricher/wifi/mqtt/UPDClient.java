package org.sunricher.wifi.mqtt;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;

public class UPDClient {
	private String ip = "192.168.155.19";
	private String mac = "ACCF23CD3B50";
	private InetAddress inetAddress = null;
	private DatagramSocket ds = null;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void start() {
		try {
			String network_address = StringUtils.substringBeforeLast(ip, ".") + ".255";
			System.out.println("$    network_address = " + network_address);
			inetAddress = InetAddress.getByName(network_address);

			setDs(new DatagramSocket(48899));
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
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

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
}
