package com.sudoteam.securitycenter.service;

import com.sudoteam.securitycenter.deviceutils.BatteryInfoHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BatteryChangeService extends Service{
	
	private static final boolean DEBUG = true;
	private static final String TAG = "BatteryChangeService";

	private static BatteryInfoHelper batteryHelper;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		log_e("BatteryChangeService onCreate");
		batteryHelper = BatteryInfoHelper.getInstance();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		log_e("BatteryChangeService onStartCommand");
		batteryHelper.registerBatteryChangeReceiver(getApplicationContext());
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		log_e("BatteryChangeService onDestroy");
		batteryHelper.unregisterBatteryChangeReceiver(getApplicationContext());
	}
	
	private static void log_e(String msg){
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}
	

}










