package org.sunricher.wifi.mqtt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TcpClient {
	private ScheduledExecutorService executor;
	private OutputStream outputStream = null;
	private Socket socket = null;
	private static final int keepAliveSeconds = 280;
	private String host;
	private int port;
	ScheduledFuture<?> future;
	private boolean connecctionInProgress = false;

	public TcpClient(String aHost, int aPort) {
		this.host = aHost;
		this.port = aPort;
	}

	public void init() {
		executor = Executors.newScheduledThreadPool(1);
		future = executor.scheduleAtFixedRate(new TcpHeartBeat(this), keepAliveSeconds, keepAliveSeconds,
				TimeUnit.SECONDS);
		connect();
	}

	public void shutDown() {
		future.cancel(true);
		disconnect();
	}

	public void connect() {

		if (connecctionInProgress) {
			System.out.println(
					"Reconnecting to LED Controller will be ommitted because another process is trying to connect.");
			return;
		}

		connecctionInProgress = true;

		// close everything in case it was already connected
		disconnect();
		boolean connected = false;
		System.out.println("Connecting to LED Controller...");

		while (connected == false) {
			try {

				InetSocketAddress addr = new InetSocketAddress(host, port);
				socket = new Socket();
				socket.setSoTimeout(4000);
				socket.connect(addr, 4000);
				socket.sendUrgentData(255);
				outputStream = socket.getOutputStream();
				Thread.sleep(300);
				System.out.println("Connected to LED Controller");
				connected = true;
			} catch (UnknownHostException e) {
				System.out.println("Host unknown. Connection failed." + e.getStackTrace());
				return;
			} catch (InterruptedException | IOException e) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// we cannot do much here.
				}
			}
		}
	}

	private void disconnect() {
		if (outputStream != null) {
			try {
				outputStream.close();
				outputStream = null;
				socket.close();
				socket = null;
				System.out.println("Disconnected from LED Controller");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public OutputStream getOs() {
		return outputStream;
	}
}
