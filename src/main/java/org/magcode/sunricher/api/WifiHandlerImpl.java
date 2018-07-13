package org.magcode.sunricher.api;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.magcode.sunricher.mqtt.TcpClient;

public class WifiHandlerImpl implements WifiHandler {
	private static Logger logger = LogManager.getLogger(WifiHandlerImpl.class);
	private int repeat = 4;
	private TcpClient tcpClient;
	private final int SLEEP = 8;
	private final int SLEEP_AT_END = 7;

	public WifiHandlerImpl(TcpClient aTcpClient, int repeat) {
		this.repeat = repeat;
		this.tcpClient = aTcpClient;
	}

	@Override
	public void setRGB(List<Integer> zones, int r, int g, int b) {
		try {
			this.setR(zones, r);
			Thread.sleep(SLEEP);
			this.setG(zones, g);
			Thread.sleep(SLEEP);
			this.setB(zones, b);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	@Override
	public void setHSB(List<Integer> zones, int h, int s, int v) {
		Color color = Color.getHSBColor((float) h / 360, (float) s / 100, (float) v / 100);
		this.setRGB(zones, color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	public void setR(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 255);
		byte[] data = Constants.DATA_RGB_R;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	private int cleanValue(int value, int min, int max) {
		int ret = value;
		if (value > max) {
			ret = max;
		}
		if (value < min) {
			ret = min;
		}
		return ret;
	}

	@Override
	public void setG(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 255);
		byte[] data = Constants.DATA_RGB_G;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	@Override
	public void setB(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 255);
		byte[] data = Constants.DATA_RGB_B;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	@Override
	public void setW(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 255);
		byte[] data = Constants.DATA_RGB_W;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	@Override
	public void setRGBFadeProgram(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 10);
		// 0 -> turn off
		// 1-10 -> select and start program
		if (value == 0) {
			byte[] data = Constants.DATA_RGB_PRG_OFF;
			send(this.getMessage(zones, data[0], data[1], data[2]));
		} else {
			byte[] data = Constants.DATA_RGB_PRG_ON;
			// we need to add +20 to program. The controller expects 21 to 22 it seems.
			send(this.getMessage(zones, data[0], data[1], (byte) (value + 20)));
		}
	}

	@Override
	public void setRGBFadeSpeed(List<Integer> zones, int value) {
		value = cleanValue(value, 1, 10);
		byte[] data = Constants.DATA_RGB_PRG_SPEED;
		send(this.getMessage(zones, data[0], data[1], (byte) (value)));
	}

	@Override
	public void togglePower(List<Integer> zones, boolean powerState) {
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
	public void setBrightness(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 256);
		byte[] data = Constants.DATA_BRIGHT;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	@Override
	public void setRGBBrightness(List<Integer> zones, int value) {
		value = cleanValue(value, 0, 256);
		byte[] data = Constants.DATA_RGB_BRIGHT;
		send(this.getMessage(zones, data[0], data[1], (byte) value));
	}

	/**
	 * set bit for the corresponding zone number. If array is empty no bit will be
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
	private byte[] getMessage(List<Integer> zones, byte category, byte channel, byte value) {
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
		result[8] = (byte) value;
		// checksum
		result[9] = (byte) (result[8] + result[7] + result[6] + result[5] + result[4]);
		// marker bytes
		result[10] = (byte) 0xaa;
		result[11] = (byte) 0xaa;
		return result;
	}

	private void send(byte[] bytes) {
		try {
			logger.trace("Sending {} times: {} - {} - {}", repeat, bytes[6], bytes[7], bytes[8]);
			for (int i = 1; i <= repeat; i++) {
				tcpClient.getOs().write(bytes);
				tcpClient.getOs().flush();
				Thread.sleep(SLEEP_AT_END);
			}
		} catch (IOException e) {
			logger.error("Stream write error", e);
			tcpClient.connect();
			tcpClient.getUpdClient().sendDisableWifi();
		} catch (InterruptedException e) {
			logger.error("Interrupted", e);
		}
	}
}