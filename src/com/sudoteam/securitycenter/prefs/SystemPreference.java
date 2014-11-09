package com.sudoteam.securitycenter.prefs;

import android.content.Context;
import android.content.SharedPreferences;


public class SystemPreference {

    private static SystemPreference mInstance;

    public static SystemPreference getInstance(Context context){
        if (mInstance == null){
        	synchronized (SystemPreference.class) {
        		if(mInstance == null){
        			mInstance = new SystemPreference(context);
        		}
			}
        }
        return mInstance;
    }

    private SystemPreference(Context context){
        mContext = context;
        ensureSystemPreference();
    }

    private static final String SP_NAME = "system_preference";
    private static final String KEY_IS_APP_FIRST_USE = "is_app_first_use";
    
    private static final String IS_POWER_PLAN_FIRST_USE = "is_power_plan_first_use";
    private static final String IS_CLOCK_MODE_FIRST_USE = "is_clock_mode_first_use";

    private Context mContext;
    private SharedPreferences mSp;

    //---------------定时模式第一次启动
    public boolean isClockModeFirstLaunch(){
    	ensureSystemPreference();
    	return  mSp.getBoolean(IS_CLOCK_MODE_FIRST_USE, true);
    }
    
    public void setClockModeFirstLaunchFalse(){
    	ensureSystemPreference();
    	mSp.edit().putBoolean(IS_CLOCK_MODE_FIRST_USE, false).commit();
    }
    
    //---------------电源方案 第一次启动
    public boolean isPowerManagerFirstLaunch(){
        ensureSystemPreference();
        return  mSp.getBoolean(IS_POWER_PLAN_FIRST_USE, true);
    }

    public void setPowerManagerFirstLaunchFalse(){
        ensureSystemPreference();
        mSp.edit().putBoolean(IS_POWER_PLAN_FIRST_USE, false).commit();
    }
    
    //---------------app 第一次启动
    public boolean isAppFirstLaunch(){

        ensureSystemPreference();
        return  mSp.getBoolean(KEY_IS_APP_FIRST_USE, true);

    }

    public void setFirstLaunchFalse(){
        ensureSystemPreference();
        mSp.edit().putBoolean(KEY_IS_APP_FIRST_USE, false).commit();
    }

    private void ensureSystemPreference(){
        if (mSp == null){
            mSp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
    }

}
