package com.sudoteam.securitycenter.deviceutils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManager;

public class BrightnessSettings {
	
	private static final boolean DEBUG = true;
	private static final String TAG = "BrightnessSettings";  
    /** 可调节的最小亮度值 */  
    public static final int MIN_BRIGHTNESS = 30;  
    /** 可调节的最大亮度值 */  
    public static final int MAX_BRIGHTNESS = 255;  
    
    private static BrightnessSettings sInstance;
    
    public static BrightnessSettings getInstance(Context context){
    	if(sInstance == null){
    		synchronized (BrightnessSettings.class) {
    			if (sInstance == null) {
    				sInstance = new BrightnessSettings(context);
				}
			}
    	}
    	return sInstance;
    }
    
	private Context mContext;  
	private final IPowerManager mPower;
	
	private BrightnessSettings(Context context){
		mContext = context;
		mPower = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
	}
	
	
	/** 
     * 获得当前系统的亮度模式 
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度 
     */  
    public int getBrightnessMode() {  
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;  
        try {
			brightnessMode = Settings.System.getInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (SettingNotFoundException e) {
			Log.e(TAG, "获得当前屏幕的亮度模式失败：Exception:"+e.getMessage());  
		}  
        return brightnessMode;  
    }  
	  
    /** 
     * 设置当前系统的亮度模式 
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度 
     */  
    public void setBrightnessMode(int brightnessMode) {  
        try {  
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode);  
        } catch (Exception e) {  
            Log.e(TAG, "设置当前屏幕的亮度模式失败：", e);  
        }  
    }  
	
    /** 
     * 获得当前系统的亮度值： 0~255 
     */  
    public int getSysScreenBrightness() {  
        int screenBrightness = MAX_BRIGHTNESS;  
        try {  
            screenBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
        } catch (Exception e) {  
            Log.e(TAG, "获得当前系统的亮度值失败：", e);  
        }  
        return screenBrightness;  
    }  
    
    public void setBrightness(int brightness) {
        try {
            mPower.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException ex) {
        	if (DEBUG) Log.e(TAG, "setBrightness Exception:"+ex.getMessage());
        }
    }
	 
    /** 
     * 设置当前系统的亮度值:0~255 
     */  
    public void setSysScreenBrightness(int brightness) {  
        try {  
            ContentResolver resolver = mContext.getContentResolver();  
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);  
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);  
            resolver.notifyChange(uri, null); // 实时通知改变  
        } catch (Exception e) {  
            Log.e(TAG, "设置当前系统的亮度值失败：", e);  
        }  
    }  
	
    /** 
     * 设置屏幕亮度，这会反映到真实屏幕上 
     *  
     * @param activity 
     * @param brightness 
     */  
    public void setActScreenBrightness(final int brightness) {  
        final WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();  
        lp.screenBrightness = brightness / (float) MAX_BRIGHTNESS;  
        ((Activity)mContext).getWindow().setAttributes(lp);  
    }  
	    
    /** 
     * 还原亮度模式和亮度值的设置 
     *  
     * @param act 
     * @param brightnessMode 
     * @param brightness 
     */  
    private void recoverBrightnessSetting(final Activity act, final int brightnessMode, final int brightness) {  
        setBrightnessMode(brightnessMode);  
        setSysScreenBrightness(brightness);  
        setActScreenBrightness(-MAX_BRIGHTNESS);  
    }    
	    
}
