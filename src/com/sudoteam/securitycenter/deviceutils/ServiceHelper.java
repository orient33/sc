package com.sudoteam.securitycenter.deviceutils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Created by huhuajun on 14-10-30.
 */
public class ServiceHelper {

    public static WifiManager getWifiManager(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager;
    }

    public static BluetoothAdapter getLocalBluetoothAdapter(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter;
    }
    
    public static ConnectivityManager getConnectivityManager(Context context){
    	 ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	 return cm;
    }
    
    public static LocationManager getLocationManager(Context context){
    	 LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
    	return locationManager;
    }
    
    public static AudioManager getAudioManager(Context context){
    	AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	return audioManager;
    }
    
    public static PowerManager getPowerManager(Context context){
    	PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    	return powerManager;
    }
    
}
