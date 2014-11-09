package com.sudoteam.securitycenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.sudoteam.securitycenter.datacell.BatteryChargeCell;
import com.sudoteam.securitycenter.deviceutils.BatteryInfoHelper;

public class PowerComputeService extends IntentService {

	private static final boolean DEBUG = true;
	private static final String TAG = "PowerComputeService";

	private static BatteryChargeCell sBatteryChargeInfo;

	public PowerComputeService() {
		super("PowerComputeService");
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

			// 判断手机电池状态是否变化
			if (batteryStatus == sBatteryChargeInfo.chargeStatus) {
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
						sBatteryChargeInfo.chargeStatus = batteryStatus;
						sBatteryChargeInfo
								.insertSelfToDb(getApplicationContext());
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
				revertBatteryChargeInfo(sBatteryChargeInfo, batteryStatus, batteryLevel);
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
	
	private static void log_e(String msg){
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}
