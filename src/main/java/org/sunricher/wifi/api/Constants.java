package org.sunricher.wifi.api;

public class Constants {

	public static final byte[] DATA_OFF;
	public static final byte[] DATA_ON;
	public static final byte[] DATA_DIM_WHITE;
	public static final byte[] DATA_ROOM1_OFF;
	public static final byte[] DATA_ROOM1_ON;
	public static final byte[] DATA_ROOM2_OFF;
	public static final byte[] DATA_ROOM2_ON;
	public static final byte[] DATA_ROOM3_OFF;
	public static final byte[] DATA_ROOM3_ON;
	public static final byte[] DATA_ROOM4_OFF;
	public static final byte[] DATA_ROOM4_ON;
	public static final byte[] DATA_ROOM5_OFF;
	public static final byte[] DATA_ROOM5_ON;
	public static final byte[] DATA_ROOM6_OFF;
	public static final byte[] DATA_ROOM6_ON;
	public static final byte[] DATA_ROOM7_OFF;
	public static final byte[] DATA_ROOM7_ON;
	public static final byte[] DATA_ROOM8_OFF;
	public static final byte[] DATA_ROOM8_ON;

	public static final String AT_OK = "+ok";
	public static final String AT_ASSIST = "HF-A11ASSISTHREAD";

	public static final int TCP_PORT = 8899;
	public static final int UDP_PORT = 48899;
	static {
		DATA_OFF = new byte[] { (byte) 2, (byte) 18, (byte) -87 };
		DATA_ON = new byte[] { (byte) 2, (byte) 18, (byte) -85 };
		DATA_ROOM1_OFF = new byte[] { 2, 10, -110 };
		DATA_ROOM2_OFF = new byte[] { 2, 10, -107 };
		DATA_ROOM3_OFF = new byte[] { 2, 10, -104 };
		DATA_ROOM4_OFF = new byte[] { 2, 10, -101 };
		DATA_ROOM5_OFF = new byte[] { 2, 10, -98 };
		DATA_ROOM6_OFF = new byte[] { 2, 10, -95 };
		DATA_ROOM7_OFF = new byte[] { 2, 10, -92 };
		DATA_ROOM8_OFF = new byte[] { 2, 10, -89 };
		DATA_ROOM1_ON = new byte[] { 2, 10, -109 };
		DATA_ROOM2_ON = new byte[] { 2, 10, -106 };
		DATA_ROOM3_ON = new byte[] { 2, 10, -103 };
		DATA_ROOM4_ON = new byte[] { 2, 10, -100 };
		DATA_ROOM5_ON = new byte[] { 2, 10, -97 };
		DATA_ROOM6_ON = new byte[] { 2, 10, -94 };
		DATA_ROOM7_ON = new byte[] { 2, 10, -91 };
		DATA_ROOM8_ON = new byte[] { 2, 10, -88 };
		byte[] bArr = new byte[3];
		bArr[0] = (byte) 8;
		bArr[1] = (byte) 56;
		DATA_DIM_WHITE = bArr;
	}
}