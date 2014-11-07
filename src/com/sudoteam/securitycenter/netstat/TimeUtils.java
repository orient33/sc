package com.sudoteam.securitycenter.netstat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/***/
public class TimeUtils {

    public static final int ONE_DAY = 24 * 3600 * 1000;

    /**
     * eg. yyyy-MM-dd HH:mmZ '1969-12-31 16:00+0800'
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 获取日期date的时间起始时间long 如 2014-11-11 ---> (2014-11-11 00:00:00)
     */
    static long getLongForDate(String date) {
        try {
            return sdf2.parse(date).getTime();
        } catch (ParseException e) {
            throw new RuntimeException("date format is error.  " + date);
        }
    }

    static List<String> getDatesOfLong(long time) {
        final String date = sdf2.format(new Date(time));
        String hao = date.substring(8, 10); // [01--31]
        int _hao = Integer.parseInt(hao);
        ArrayList<String> times = new ArrayList<String>();
        if (_hao < 1)
            throw new RuntimeException("日期怎么可能小于 1 ");
        if (_hao == 1) {
            times.add(date);
            return times;
        } else {
            String pre = date.substring(0, 8);//2014-11-
            for (int i = 1; i <= _hao; ++i) {
                String _date = pre + (i < 10 ? "0" + i : "" + i);
                times.add(_date);
            }
        }
        return times;
    }

    static long getStartForMonth(long now) {
        String replace = "01 00:00";
        return getStartForCurrent(now, 8, 16, replace);
    }

    static long getStartForDay(long now) {
        String replace = "00:00";
        return getStartForCurrent(now, 11, 16, replace);
    }

    private static long getStartForCurrent(long now, int index1, int index2, String replace) {
        String now_s = sdf.format(new Date(now));
        // index [8--16) -> 01 00:00
        String s = now_s.substring(0, index1) + replace + now_s.substring(index2);
//        i("now is " + now_s + ", result is " + s);
        // now is 2014-11-06 09:27+0800, result is 2014-11-06 00:00+0800
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
