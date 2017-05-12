package org.sunricher.wifi.mqtt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TcpClient {
	private ScheduledExecutorService executor;
	private OutputStream os = null;
	private Socket lkSocket = null;
	private String host;
	private int port;
	ScheduledFuture<?> future;

	public TcpClient(String aHost, int aPort) {
		this.host = aHost;
		this.port = aPort;
	}

	public void init() {
		executor = Executors.newScheduledThreadPool(1);
		future = executor.scheduleAtFixedRate(new TcpHeartBeat(this), 10, 280, TimeUnit.SECONDS);

		connect();
	}

	public void connect() {
		try {
			lkSocket = new Socket(host, port);
			lkSocket.sendUrgentData(255);
			os = lkSocket.getOutputStream();
			// get shure that the controller gets some time before the first
			// request will be send
			Thread.sleep(200);
			System.out.println("Connected");
		} catch (InterruptedException e) {
			// we cannot do much here.
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		future.cancel(true);
		if (os != null) {
			try {
				os.close();
				os = null;
				lkSocket.close();
				lkSocket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void reconnect() {
		if (os != null) {
			try {
				os.close();
				os = null;
				lkSocket.close();
				lkSocket = null;
				this.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	public OutputStream getOs() {
		return os;
	}

}
