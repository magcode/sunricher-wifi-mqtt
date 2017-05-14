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
import org.sunricher.wifi.api.Constants;

public class UPDClient {
	private String host;
	private InetAddress inetAddress = null;
	private DatagramSocket ds = null;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}