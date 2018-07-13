package org.magcode.sunricher.api;

import java.util.List;

public interface WifiHandler {
	public void setRGB(List<Integer> zones, int r, int g, int b);
	public void setHSB(List<Integer> zones, int h, int s, int v);
	public void setR(List<Integer> zones, int value);
	public void setG(List<Integer> zones, int value);
	public void setB(List<Integer> zones, int value);
	public void setW(List<Integer> zones, int value);
	public void togglePower(List<Integer> zones, boolean powerState);
	public void setBrightness(List<Integer> zones, int value);
	public void setRGBFadeSpeed(List<Integer> zonesA, int value);
	public 	void setRGBFadeProgram(List<Integer> zones, int value);
	public 	void setRGBBrightness(List<Integer> zones, int value);
}