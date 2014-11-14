package com.sudoteam.securitycenter.optimizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.ICheck;
import com.sudoteam.securitycenter.R;

/**
 */
public class CheckOptimizer implements ICheck {
    static final long ONE_HOUR = 60 * 60 * 1000;
    static final long ONE_DAY = 24 * ONE_HOUR;
    static final long ONE_WEEK = 7 * ONE_DAY;

    @Override
    public CheckResult doCheck(Context context) {
        final CheckResult cr = new CheckResult();
        cr.name = context.getString(R.string.module_optimizer);
        long now = System.currentTimeMillis();
        long last = getLastTimeForOptimize(context);
        int diff = (int) (now - last);
        if( last <= 0) {
            cr.type = CheckResult.TYPE_MANUAL;
            cr.content = context.getString(R.string.clear_never);
        }else if (diff < ONE_HOUR) {
            // just now
            cr.type = CheckResult.TYPE_PASSED;
            cr.content = context.getString(R.string.clear_just_now);
        } else if (diff < ONE_DAY) {
            int hour = (int) (now - last);
            cr.type = CheckResult.TYPE_MANUAL;
            cr.content = context.getString(R.string.clear_sevel_hour, hour+"");
        } else if (diff < ONE_WEEK) {
            int day = (int) (diff / ONE_DAY);
            cr.content = context.getString(R.string.clear_sevel_day, day + "");
            cr.type = CheckResult.TYPE_MANUAL;
        }else if(diff < ONE_WEEK * 4){
            int week = (int)(diff / ONE_DAY / 7);
            cr.type = CheckResult.TYPE_MANUAL;
            cr.content = context.getString(R.string.clear_sevel_week, week+"");
        } else{
            int month = (int)(diff / ONE_DAY / 30);
            cr.type = CheckResult.TYPE_MANUAL;
            cr.content = context.getString(R.string.clear_sevel_month, month+"");
        }
        cr.callback = new CheckResult.IFix() {
            @Override
            public boolean doFix(Context context) {
                Intent intent = new Intent(context, OptimizerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        };
        return cr;
    }

    static final String KEY_LAST_OPTIMIZE = "key-last-opt";

    static long getLastTimeForOptimize(Context c) {
        SharedPreferences sp = c.getSharedPreferences(c.getPackageName(), 0);
        return sp.getLong(KEY_LAST_OPTIMIZE, 0);
    }

    static void setLastTimeForOptimize(Context c){
        SharedPreferences.Editor editor = c.getSharedPreferences(c.getPackageName(),0).edit();
        editor.putLong(KEY_LAST_OPTIMIZE,System.currentTimeMillis());
        editor.commit();
    }
}
