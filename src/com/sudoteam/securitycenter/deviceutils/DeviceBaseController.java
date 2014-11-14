package com.sudoteam.securitycenter.deviceutils;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.view.RotationPolicy;
import com.sudoteam.securitycenter.R;

/**
 * Created by huhuajun on 14-10-30.
 */
public class DeviceBaseController {

    private static final boolean DEBUG = true;
    private static final String TAG = "DevicesSwitcher";
    private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;
    
    public static boolean isWifiOn(Context context){
    	WifiManager wifiManager = ServiceHelper.getWifiManager(context);
    	return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ? true : false;
    }
    
    /**
     * 设置wifi
     * @param context
     * @param isOpen
     */
    public static boolean setWifiEnabled(Context context,boolean enabled){
    	 // Show toast message if Wi-Fi is not allowed in airplane mode
        if (enabled && !isRadioAllowed(context, Settings.Global.RADIO_WIFI)) {
            Toast.makeText(context,R.string.in_airplane_mode, Toast.LENGTH_SHORT).show();
            return false;
        }
        WifiManager wifiManager = ServiceHelper.getWifiManager(context);
        // Disable tethering if enabling Wifi
        if(enabled){
        	int wifiState = wifiManager.getWifiState();
        	if(wifiState == WifiManager.WIFI_STATE_ENABLED){
        		return true;
        	}else if(wifiState == WifiManager.WIFI_STATE_ENABLING){
        		wifiManager.setWifiEnabled(false);
        	}
        }
        if (!wifiManager.setWifiEnabled(enabled)) {
            // Error
            log_e("setWifiSwitcher error");
            return false;
        }
        return true;
    }

    public static boolean isBluetoothOn(){
    	BluetoothAdapter bluetoothAdapter = ServiceHelper.getLocalBluetoothAdapter();
    	return bluetoothAdapter == null ? false:bluetoothAdapter.isEnabled();
    }
    
    /**
     * 设置蓝牙
     * @param context
     * @param isOpen
     * @return
     */
    public static boolean setBluetoothEnabled(Context context,boolean enabled){
    	if (enabled &&!isRadioAllowed(context, Settings.Global.RADIO_BLUETOOTH)) {
            Toast.makeText(context, R.string.in_airplane_mode, Toast.LENGTH_SHORT).show();
            return false;
        }
    	BluetoothAdapter bluetoothAdapter = ServiceHelper.getLocalBluetoothAdapter();
        boolean result = false;
        if (bluetoothAdapter == null){
        	log_e("该设备不支持蓝牙");
        }else{
            result = enabled ? bluetoothAdapter.enable(): bluetoothAdapter.disable();
        }
        return result;

    }

    /**
     * wifi,bluetooth,nfc在飞行模式下是否允许
     * @param context
     * @param type
     * @return
     */
    private static boolean isRadioAllowed(Context context, String type) {
        if (!isAirplaneModeOn(context)) {
            return true;
        }
        // Here we use the same logic in onCreate().
        String toggleable = Settings.Global.getString(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS);
        return toggleable != null && toggleable.contains(type);
    }

    /**
     * 飞行模式是否开启
     * @param context
     * @return
     */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**
     * 设置飞行模式
     * @param context
     * @param enabling
     */
    public static boolean setAirPlanModeEnabled(Context context,boolean enabled) {
        // Change the system setting
        boolean bool = Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enabled ? 1 : 0);
        // Post the intent
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabled);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
        return bool;
    }
    
    /**
     * 移动数据是否可用
     * @param context
     * @return
     */
    public static boolean isMobileDataOn(Context context){
    	return ServiceHelper.getConnectivityManager(context).getMobileDataEnabled();
    }
    
    /**
     * 设置移动网络
     * @param context
     * @param enabled
     */
    public static void setMobileDataEnabled(Context context,boolean enabled){
    	ConnectivityManager cm = ServiceHelper.getConnectivityManager(context);
    	cm.setMobileDataEnabled(enabled);
    }
    
    
    /**
     * 屏幕是否自定旋转
     * @return
     */
    public static boolean isAutoRotateScreenOn(Context context){
    	return RotationPolicy.isRotationLocked(context);
    }
    
    /**
     * 设置自动旋转屏幕
     * @param context
     * @param enabled
     */
    public static void setAutoRotateScreen(Context context,boolean enabled){
    	RotationPolicy.setRotationLockForAccessibility(context, enabled);
    }
    
    /**
     * gps是否打开（gps和network，其中一个开就算开）
     * @param context
     * @return
     */
    public static boolean isGpsOn(Context context) {  
        LocationManager locationManager = ServiceHelper.getLocationManager(context);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        log_e("gps:"+gps+" network:"+network);
        if (gps || network) {  
            return true;  
        }  
        return false;  
    }  
    
    /**
     * 设置gps
     * @param context
     * @param enabled
     */
    public static void setGPS(Context context,boolean enabled){
    	Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER, enabled);  
    	Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.NETWORK_PROVIDER, enabled);  
    }
    
    public static boolean isHapticFeedbackOn(Context context){
    	 return Settings.System.getInt(context.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
    }
    
    /**
     * 设置触摸是是否振动
     * @param context
     * @return
     */
    public static void setHapticFeedback(Context context,boolean enabled){
    	Settings.System.putInt(context.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED,
    			enabled ? 1 : 0);
    }
    
    /**
     * 获取当前超时锁屏时间
     * @param context
     * @return 单位 （s）
     */
    public static int getLockedScreenTimeout(Context context){
    	long currentTimeout = Settings.System.getLong(context.getContentResolver(), SCREEN_OFF_TIMEOUT,
                FALLBACK_SCREEN_TIMEOUT_VALUE);
    	return (int)(currentTimeout/1000);
    }
    
    /**
     * 设置超时锁屏时间
     * @param context
     * @param lockedTimeout （单位 s）
     */
    public static void setLockedScreenTimeout(Context context,int lockedTimeout){
    	 Settings.System.putInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, lockedTimeout*1000);
    }
    
    public static int getCurrentTypeVolume(Context context,int soundType){
    	return ServiceHelper.getAudioManager(context).getStreamVolume(soundType);
    }
    
    public static int getCurrentTypeMaxVolume(Context context,int soundType){
    	return ServiceHelper.getAudioManager(context).getStreamMaxVolume(soundType);
    }
    
    /**
     * 设置来电铃声大小
     * @param context
     * @param volume
     */
    public static void setRingerVolume(Context context,int volume){
    	AudioManager audioManager = ServiceHelper.getAudioManager(context);
    	audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
    }
    
    /**
     * 设置通知音大小
     * @param context
     * @param volume
     */
    public static void setNotifyVolume(Context context,int volume){
    	AudioManager audioManager = ServiceHelper.getAudioManager(context);
    	audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
    }
    
    /**
     * 设置闹钟大小
     * @param context
     * @param volume
     */
    public static void setAlarmVolume(Context context,int volume){
    	AudioManager audioManager = ServiceHelper.getAudioManager(context);
    	audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
    }
    
    public static boolean isVirateWhenRingingOn(Context context){
    	return Settings.System.getInt(context.getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, 0) != 0;
    }
    
    public static void setVirateWhenRinging(Context context,boolean enabled){
    	 Settings.System.putInt(context.getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, enabled ? 1 : 0);
    }
    
    private static void log_e(String msg){
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}
    


}
