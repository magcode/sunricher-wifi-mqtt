package org.sunricher.wifi.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.magcode.sunricher.mqtt.MqttSubscriber;
import org.magcode.sunricher.mqtt.TcpClient;

public class ColorHandlerImpl implements ColorHandler {
	private static Logger logger = LogManager.getLogger(ColorHandlerImpl.class);
	/**
	 * sleep between two commands in a series
	 */
	final int SLEEP = 8;

	/**
	 * sleep at the end of a command series
	 */
	final int SLEEP_AT_END = 7;

	private TcpClient tcpClient;

	public ColorHandlerImpl(TcpClient aTcpClient) {
		tcpClient = aTcpClient;
	}

	@Override
	public void setRGB(List<Integer> zones, int r, int g, int b) throws IOException, InterruptedException {

	}

	@Override
	public void setHSV(List<Integer> zones, int h, int s, int v) throws IOException, InterruptedException {

	}

	@Override
	public void setRGBWithWhiteChannel(List<Integer> zones, int r, int g, int b, boolean maxBrightness)
			throws IOException, InterruptedException {

	}

	@Override
	public void setHSVwithWihiteChannel(List<Integer> zones, int h, int s, int v, boolean maxBrightness) {

	}

	@Override
	public void setR(List<Integer> zones, int value) throws IOException, InterruptedException {

	}

	@Override
	public void setG(List<Integer> zones, int value) throws InterruptedException, IOException {
	}

	@Override
	public void setB(List<Integer> zones, int value) throws InterruptedException, IOException {

	}

	@Override
	public void setW(List<Integer> zones, int value) throws InterruptedException, IOException {

	}

	@Override
	public void resetColor(List<Integer> zones) throws IOException, InterruptedException {
		this.setRGB(zones, 0, 0, 0);
		this.setW(zones, 0);
		this.setBrightness(zones, 7);
	}

	@Override
	public void togglePower(boolean powerState) throws IOException {
		// works
		byte[] data = powerState ? Constants.DATA_ON : Constants.DATA_OFF;
		// os.write(this.getMessage(new ArrayList<Integer>(), data[0], data[1],
		// data[2]));
	}

	@Override
	public void togglePower(List<Integer> zones, boolean powerState) throws IOException, InterruptedException {
		// works
		byte[] data = null;
		for (int zone : zones) {

			switch (zone) {
			case 1:
				if (powerState) {
					data = Constants.DATA_ROOM1_ON;
				} else {
					data = Constants.DATA_ROOM1_OFF;
				}
				break;
			case 2:
				if (powerState) {
					data = Constants.DATA_ROOM2_ON;
				} else {
					data = Constants.DATA_ROOM2_OFF;
				}
				break;
			case 3:
				if (powerState) {
					data = Constants.DATA_ROOM3_ON;
				} else {
					data = Constants.DATA_ROOM3_OFF;
				}
				break;
			case 4:
				if (powerState) {
					data = Constants.DATA_ROOM4_ON;
				} else {
					data = Constants.DATA_ROOM4_OFF;
				}
				break;
			case 5:
				if (powerState) {
					data = Constants.DATA_ROOM5_ON;
				} else {
					data = Constants.DATA_ROOM5_OFF;
				}
				break;
			case 6:
				if (powerState) {
					data = Constants.DATA_ROOM6_ON;
				} else {
					data = Constants.DATA_ROOM6_OFF;
				}
				break;
			case 7:
				if (powerState) {
					data = Constants.DATA_ROOM7_ON;
				} else {
					data = Constants.DATA_ROOM7_OFF;
				}
				break;
			case 8:
				if (powerState) {
					data = Constants.DATA_ROOM8_ON;
				} else {
					data = Constants.DATA_ROOM8_OFF;
				}
				break;
			}

		}
		if (null != data) {
			send(this.getMessage(zones, data[0], data[1], data[2]));
		}
	}

	@Override
	public void setBrightness(List<Integer> zones, int value) throws InterruptedException, IOException {
		// works
		if (value > 255) {
			value = 255;
		}
		if (value < 0) {
			value = 0;
		}

		byte[] data = Constants.DATA_DIM_WHITE;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	@Override
	public void toggleColorFader(List<Integer> zones) throws IOException, InterruptedException {
	}

	@Override
	public void speedUpColorFader(List<Integer> zones) throws IOException, InterruptedException {

	}

	@Override
	public void speedDownColorFader(List<Integer> zones) throws IOException, InterruptedException {

	}

	/**
	 * set bit for the corresponding zonenumber. If array is empty no bit will be
	 * set.
	 * 
	 * @param zones
	 * @return
	 */
	private byte generateZoneByte(List<Integer> zones) {
		if (zones.size() == 0)
			return 0;

		byte result = 0;
		for (int currentZone : zones) {
			if (currentZone <= 0 || currentZone > 8) {
				continue;
			}
			result = (byte) (result | (1 << currentZone - 1));
		}
		return result;
	}

	/**
	 * see {@link #getMessage(List, byte, byte, int)}
	 * 
	 * @param zone
	 * @param category
	 * @param channel
	 * @param value
	 * @return
	 */
	private byte[] getMessage(int zone, byte category, byte channel, byte value) {
		ArrayList<Integer> zoneArray = new ArrayList<Integer>();
		zoneArray.add(zone);
		return this.getMessage(zoneArray, category, channel, value);
	}

	/**
	 * create message for LK35.
	 * 
	 * @param zones
	 *            zones will be set in zonebit
	 * @param category
	 *            see category constants (different remote layouts are grouped in
	 *            categories)
	 * @param channel
	 *            channel or button name
	 * @param value
	 *            constant or ar range (depends on function of that channel)
	 * @return generated message, ready to send
	 */
	private byte[] getMessage(List<Integer> zones, byte category, byte channel, byte value) {
		byte[] bytes = new byte[] { category, channel, value };

		byte[] result = new byte[12];

		// remote identifier
		result[0] = 0x55;
		result[1] = 0x33;
		result[2] = 0x61;
		result[3] = 0x39;
		result[4] = 0x02;
		// zone
		result[5] = this.generateZoneByte(zones);
		// category - rgb vaules
		result[6] = category;
		// color channel
		result[7] = channel;
		// value
		result[8] = value;
		// checksum
		result[9] = (byte) (result[8] + result[7] + result[6] + result[5] + result[4]);
		// marker bytes
		result[10] = (byte) 0xaa;
		result[11] = (byte) 0xaa;
		// System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(result));
		return result;
	}

	@Override
	public void saveCurrentColor(List<Integer> zones, int slot) throws IOException, InterruptedException {

	}

	private void send(byte[] bytes) {
		try {
			tcpClient.getOs().write(bytes);
			tcpClient.getOs().flush();
			tcpClient.getOs().write(bytes);
			tcpClient.getOs().flush();
			tcpClient.getOs().write(bytes);
			tcpClient.getOs().flush();
			Thread.sleep(SLEEP_AT_END);
		} catch (IOException e) {
			logger.error("Stream write error", e);
			tcpClient.connect();
			tcpClient.getUpdClient().sendDisableWifi();
		} catch (InterruptedException e) {
			logger.error("Interrupted", e);
		}
	}
}
