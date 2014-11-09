package com.sudoteam.securitycenter.datacell;

import java.io.Serializable;

public class BatteryInfoCell implements Serializable{

	public int batteryStatus;
	public int batteryHealth;
	public boolean batteryPresent;
	public int batteryLevel;
	public int batteryScale;
	public int batteryIconSmall;
	public int batteryPlugged;
	public int batteryVoltage;
	public int batteryTemperature;
	public String batteryTechnology;
	
	@Override
	public String toString() {
		return "BatteryInfo{" +
                "batteryStatus=" + batteryStatus +
                ", batteryHealth=" + batteryHealth +
                ", batteryPresent='" + batteryPresent + '\'' +
                ", batteryLevel='" + batteryLevel + '\'' +
                ", batteryScale=" + batteryScale +
                ", batteryIconSmall=" + batteryIconSmall +
                ", batteryPlugged=" + batteryPlugged +
                ", batteryVoltage=" + batteryVoltage +
                ", batteryTemperature=" + batteryTemperature +
                ", batteryTechnology=" + batteryTechnology  +
                '}';
	}
	
}
