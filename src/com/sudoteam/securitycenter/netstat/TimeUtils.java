package com.sudoteam.securitycenter.netstat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***/
public class TimeUtils {

	public static final int ONE_DAY = 24 *3600 *1000;
	
	/** eg. yyyy-MM-dd HH:mmZ '1969-12-31 16:00+0800' */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");

	static long getStartForMonth(long now){
		String replace = "01 00:00";
		return getStartForCurrent(now, 8, 16, replace);
	}
	static long getStartForDay(long now){
		String replace = "00:00";
		return getStartForCurrent(now, 11, 16, replace);
	} 
	
	private static long getStartForCurrent(long now, int index1,int index2, String replace) {
		String now_s = sdf.format(new Date(now));
		// index [8--16) -> 01 00:00
		String s = now_s.substring(0, index1) + replace + now_s.substring(index2);
		i("now is " + now_s + ", result is " + s);
		try {
			return sdf.parse(s).getTime();
		} catch (ParseException e) {
			e("Date parse Error." + e.toString());
			return now - ONE_DAY;
		}
	}
	
	static void i(String s) {
		android.util.Log.i("godin", " " + s);
	}

	static void e(String s) {
		android.util.Log.e("godin", " " + s);
	}
}
