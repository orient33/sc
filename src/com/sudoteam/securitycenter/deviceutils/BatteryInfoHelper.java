package com.sudoteam.securitycenter.deviceutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.sudoteam.securitycenter.datacell.BatteryInfoCell;
import com.sudoteam.securitycenter.service.PowerComputeService;
import com.sudoteam.securitycenter.utils.ConvertHelper;

public class BatteryInfoHelper {

	private static final boolean DEBUG = true;
	private static final String TAG = "PowerInfoHelper";

	public static final String BATTERY_STATUS = "battery_status";
	public static final String BATTERY_LEVEL = "battery_level";
	
	public static final String BATTERY_CHANGE_KEY = "battery_change_key";
	public static final String BATTERY_CHANGE_MSG = "securitycenter.deviceutils.battery.change";

	private static final int DEFAULT_BATTERY_CAPACITY = 2800;// (单位 mA)
	private static final int DEFAULT_AC_CHARGEING = 1000;// (单位 mA/h)
	private static final int DEFAULT_USB_CHARGEING = 500;// (单位 mA/h)

	private static final int DEFAULT_DISCHARGE = 106560;// (单位 s)

	private static BatteryInfoCell sBatteryInfo;
	private static BatteryInfoHelper sPowerHelper;
	
	private static BatteryInfoReceiver sBatteryInfoReceiver; 

	private BatteryInfoHelper() {
		sBatteryInfoReceiver = new BatteryInfoReceiver();
	}

	public static BatteryInfoHelper getInstance() {
		if (sPowerHelper == null) {
			synchronized (BatteryInfoHelper.class) {
				if (sPowerHelper == null) {
					sPowerHelper = new BatteryInfoHelper();
				}
			}
		}
		return sPowerHelper;
	}
	
	public void registerBatteryChangeReceiver(Context context){
		IntentFilter batteryChange_filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(sBatteryInfoReceiver, batteryChange_filter);
	}
	
	public void unregisterBatteryChangeReceiver(Context context){
		context.unregisterReceiver(sBatteryInfoReceiver);
	}

	public boolean isCharging() {
		return sBatteryInfo != null ? (sBatteryInfo.batteryStatus == BatteryManager.BATTERY_PLUGGED_AC
				|| sBatteryInfo.batteryStatus == BatteryManager.BATTERY_PLUGGED_USB || sBatteryInfo.batteryStatus == BatteryManager.BATTERY_PLUGGED_WIRELESS)
				: false;
	}

	public int getCurrentBattery() {
		if (sBatteryInfo != null) {
			return   sBatteryInfo.batteryLevel;
		}
		return 0;
	}
	
	public int getBatteryScale() {
		if (sBatteryInfo != null) {
			return   sBatteryInfo.batteryScale;
		}
		return 0;
	}
	
	public float getBatterySurplusPercent(){
		return ((float)getCurrentBattery())/getBatteryScale();
	}
	/**
	 * 获取AC充电默认剩余时间 （单位 s）
	 * @return
	 */
	public int getDefaultAcChargeSurplusTime() {
		int defaultAcChargeSumTime = ConvertHelper.hour2Second(DEFAULT_BATTERY_CAPACITY / DEFAULT_AC_CHARGEING);
		float batteryPer = 1- getBatterySurplusPercent();
		log_e("getDefaultAcChargeSurplusTime batteryPer:"+batteryPer);
		return (int)(defaultAcChargeSumTime * batteryPer);
	}

	/**
	 * 获取USB充电默认剩余时间 （单位 s）
	 * @return
	 */
	public int getDefaultUsbChargeSurplusTime() {
		int defaultUsbChargeSumTime = ConvertHelper.hour2Second(DEFAULT_BATTERY_CAPACITY / DEFAULT_USB_CHARGEING);
		float batteryPer = 1- getBatterySurplusPercent();
		log_e("getDefaultUsbChargeSurplusTime batteryPer:"+batteryPer);
		return (int)(defaultUsbChargeSumTime * batteryPer);
	}

	/**
	 * 获取默认放电剩余时间 （单位 s）
	 * @return
	 */
	public int getDefaultDisChargeSurplusTime() {
		float batteryPer = getBatterySurplusPercent();
		log_e("getDefaultDisChargeSurplusTime batteryPer:"+batteryPer);
		return (int)(batteryPer*DEFAULT_DISCHARGE);
	}

	public static class BatteryInfoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (sBatteryInfo == null) {
				sBatteryInfo = new BatteryInfoCell();
			}
			String action = intent.getAction();
			if (DEBUG) Log.e(TAG, "onReceive action:"+action);
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				sBatteryInfo.batteryStatus = intent.getIntExtra(
						BatteryManager.EXTRA_STATUS, 0);
				sBatteryInfo.batteryHealth = intent.getIntExtra(
						BatteryManager.EXTRA_HEALTH, 0);
				sBatteryInfo.batteryPresent = intent.getBooleanExtra(
						BatteryManager.EXTRA_PRESENT, false);
				sBatteryInfo.batteryLevel = intent.getIntExtra(
						BatteryManager.EXTRA_LEVEL, 0);
				sBatteryInfo.batteryScale = intent.getIntExtra(
						BatteryManager.EXTRA_SCALE, 0);
				sBatteryInfo.batteryIconSmall = intent.getIntExtra(
						BatteryManager.EXTRA_ICON_SMALL, 0);
				sBatteryInfo.batteryPlugged = intent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, 0);
				sBatteryInfo.batteryVoltage = intent.getIntExtra(
						BatteryManager.EXTRA_VOLTAGE, 0);
				sBatteryInfo.batteryTemperature = intent.getIntExtra(
						BatteryManager.EXTRA_TEMPERATURE, 0);
				sBatteryInfo.batteryTechnology = intent
						.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
			}

			Intent batteryServiceIntent = new Intent(context,PowerComputeService.class);
			batteryServiceIntent.putExtra(BATTERY_STATUS, sBatteryInfo.batteryStatus);
			batteryServiceIntent.putExtra(BATTERY_LEVEL, sBatteryInfo.batteryLevel);
			context.startService(batteryServiceIntent);
			
			Intent updateUiIntent = new Intent(BATTERY_CHANGE_MSG);
			updateUiIntent.putExtra(BATTERY_CHANGE_KEY, sBatteryInfo);
			context.sendBroadcast(updateUiIntent);

			log_e(sBatteryInfo.toString());
		}

	}
	
	public static void log_e(String msg){
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}
