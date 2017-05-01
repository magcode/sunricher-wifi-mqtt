package org.sunricher.wifi.api;

public class Constant {

	public static final byte[] DATA_OFF;
	public static final byte[] DATA_ON;
	public static final byte[] DATA_CIRCLE_DIM;
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

	static {
		DATA_OFF = new byte[] { (byte) 2, (byte) 18, (byte) -87 };
		DATA_ON = new byte[] { (byte) 2, (byte) 18, (byte) -85 };
		DATA_ROOM1_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -110 };
		DATA_ROOM2_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -107 };
		DATA_ROOM3_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -104 };
		DATA_ROOM4_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -101 };
		DATA_ROOM5_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -98 };
		DATA_ROOM6_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -95 };
		DATA_ROOM7_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -92 };
		DATA_ROOM8_OFF = new byte[] { (byte) 2, (byte) 10, (byte) -89 };
		DATA_ROOM1_ON = new byte[] { (byte) 2, (byte) 10, (byte) -109 };
		DATA_ROOM2_ON = new byte[] { (byte) 2, (byte) 10, (byte) -106 };
		DATA_ROOM3_ON = new byte[] { (byte) 2, (byte) 10, (byte) -103 };
		DATA_ROOM4_ON = new byte[] { (byte) 2, (byte) 10, (byte) -100 };
		DATA_ROOM5_ON = new byte[] { (byte) 2, (byte) 10, (byte) -97 };
		DATA_ROOM6_ON = new byte[] { (byte) 2, (byte) 10, (byte) -94 };
		DATA_ROOM7_ON = new byte[] { (byte) 2, (byte) 10, (byte) -91 };
		DATA_ROOM8_ON = new byte[] { (byte) 2, (byte) 10, (byte) -88 };
		byte[] bArr = new byte[3];
		bArr[0] = (byte) 8;
		bArr[1] = (byte) 56;
		DATA_CIRCLE_DIM = bArr;
	}
}