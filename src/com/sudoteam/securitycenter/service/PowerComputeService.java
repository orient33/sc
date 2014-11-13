package com.sudoteam.securitycenter.service;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.sudoteam.securitycenter.datacell.BatteryChargeCell;
import com.sudoteam.securitycenter.deviceutils.BatteryInfoHelper;

public class PowerComputeService extends IntentService {

	private static final boolean DEBUG = true;
	private static final String TAG = "PowerComputeService";
	
	private static BatteryChargeCell sBatteryChargeInfo;
	
	private static final int DB_MAX_COUNT = 100;
	private static int sAcChargingCount = 0;
	private static int sUsbChargingCount = 0;

	public PowerComputeService() {
		super("PowerComputeService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		sAcChargingCount = computeBatteryStatusCountByDb(getApplicationContext(),
				BatteryManager.BATTERY_PLUGGED_AC);
		sUsbChargingCount = computeBatteryStatusCountByDb(getApplicationContext(),
				BatteryManager.BATTERY_PLUGGED_USB);
		log_e("hyy sAcChargingCount:" + sAcChargingCount + " sUsbChargingCount:"
				+ sUsbChargingCount);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		log_e("service onHandleIntent");
		synchronized (PowerComputeService.class) {
			if (sBatteryChargeInfo == null) {
				sBatteryChargeInfo = new BatteryChargeCell();
				sBatteryChargeInfo.chargeStatus = -1;
			}

			int batteryStatus = intent.getIntExtra(
					BatteryInfoHelper.BATTERY_STATUS, -1);
			int batteryLevel = intent.getIntExtra(
					BatteryInfoHelper.BATTERY_LEVEL, -1);
			int batteryPlugged = intent.getIntExtra(
					BatteryInfoHelper.BATTERY_PLUGGED, -1);

			// 判断手机电池状态是否变化 //去除放电状态
			if (batteryStatus == sBatteryChargeInfo.chargeStatus 
				&& batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
				log_e("battery status equals...batteryStatus:" + batteryStatus);
				sBatteryChargeInfo.endLevel = batteryLevel;
				int subLevel = Math
						.abs((sBatteryChargeInfo.endLevel - sBatteryChargeInfo.startLevel));
				if (subLevel > 0) {
					sBatteryChargeInfo.endTime = System.currentTimeMillis();
					long subTimeMs = sBatteryChargeInfo.endTime
							- sBatteryChargeInfo.startTime;
					int subTimeSec = (int) subTimeMs / 1000;
					if (subTimeSec > 0) {
						sBatteryChargeInfo.id = BatteryChargeCell
								.createUniqueId();
						sBatteryChargeInfo.chargeSpeed = subTimeSec / subLevel;
						sBatteryChargeInfo.chargeStatus = batteryPlugged;
						if (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
							if ((batteryPlugged == BatteryManager.BATTERY_PLUGGED_AC && sAcChargingCount < DB_MAX_COUNT)) {
								sBatteryChargeInfo.insertSelfToDb(getApplicationContext());
							}
							if (batteryPlugged == BatteryManager.BATTERY_PLUGGED_USB && sUsbChargingCount < DB_MAX_COUNT) {
								sBatteryChargeInfo.insertSelfToDb(getApplicationContext());
							}
						}
						// 重置充电信息
						revertBatteryChargeInfo(sBatteryChargeInfo,
								batteryStatus, batteryLevel);
					}
				} else {
					// 电量无变化
					log_e("battery no change batteryLevel:" + batteryLevel);
				}
			} else {
				// 把重置手机状态电池状态
				revertBatteryChargeInfo(sBatteryChargeInfo, -1, batteryLevel);
			}
		}
	}
	
	private void revertBatteryChargeInfo(BatteryChargeCell batteryChargeInfo,int batteryStatus,int batteryLevel){
		batteryChargeInfo.chargeStatus = batteryStatus;
		batteryChargeInfo.chargeSpeed = 0;
		batteryChargeInfo.startLevel = batteryLevel;
		batteryChargeInfo.endLevel = sBatteryChargeInfo.startLevel;
		batteryChargeInfo.startTime = System.currentTimeMillis();
		batteryChargeInfo.endTime = sBatteryChargeInfo.startTime;
	}
	
	private int computeBatteryStatusCountByDb(Context context, int batteryStatus) {
		int count = 0;
		ArrayList<BatteryChargeCell> batteryChargeInfoList = BatteryChargeCell
				.queryAllToDb(context);
		if (batteryChargeInfoList != null) {
			for (int i = 0; i < batteryChargeInfoList.size(); i++) {
				if (batteryChargeInfoList.get(i).chargeStatus == batteryStatus) {
					count++;
				}
			}
		}
		return count;
	}

	
	private static void log_e(String msg){
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}
