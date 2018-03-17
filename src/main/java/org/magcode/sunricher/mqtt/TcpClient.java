package org.magcode.sunricher.mqtt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpClient {
	private ScheduledExecutorService executor;
	private OutputStream outputStream = null;
	private Socket socket = null;
	private static final int keepAliveSeconds = 280;
	private String host;
	private int port;
	ScheduledFuture<?> future;
	private boolean connecctionInProgress = false;
	private static Logger logger = LogManager.getLogger(TcpClient.class);
	private UDPClient udpClient;

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
			logger.info(
					"Reconnecting to LED Controller will be ommitted because another process is trying to connect.");
			return;
		}

		connecctionInProgress = true;

		// close everything in case it was already connected
		disconnect();
		boolean connected = false;
		logger.info("Connecting to LED Controller...");

		while (connected == false) {
			try {

				InetSocketAddress addr = new InetSocketAddress(host, port);
				socket = new Socket();
				socket.setSoTimeout(4000);
				socket.connect(addr, 4000);
				socket.sendUrgentData(255);
				outputStream = socket.getOutputStream();
				Thread.sleep(300);
				logger.info("Connected to LED Controller");
				connected = true;
				connecctionInProgress = false;			
			} catch (UnknownHostException e) {
				logger.error("Host unknown. Connection failed.", e);
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
				logger.info("Disconnected from LED Controller");
			} catch (IOException e) {
				logger.error("Error during disconnected", e);
			}
		}
	}

	public OutputStream getOs() {
		return outputStream;
	}

	public UDPClient getUpdClient() {
		return udpClient;
	}

	public void setUpdClient(UDPClient updClient) {
		this.udpClient = updClient;
	}
}
