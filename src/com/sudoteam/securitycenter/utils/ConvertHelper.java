package com.sudoteam.securitycenter.utils;

public class ConvertHelper {
	
	/**
	 * 秒转化小时，取整数部分
	 * @param second
	 * @return
	 */
	public static int second2Hour(int second){
		return second / 3600;
	}
		
	public static int second2Minute(int second){
		return (second % 3600)/60;
	}
	
	public static int hour2Second(float hour){
		return (int) (hour * 3600);
	}
	

}
