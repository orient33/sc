package com.sudoteam.securitycenter.datacell;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sudoteam.securitycenter.constant.PowerManagerKey.BatteryModeInfoKey;

/**
 * Created by huhuajun on 14-10-22.
 */
public class DataEntityFactory {


    public static BatteryModeCell createBatteryModeEntity(String powerPlanJsonData) throws JSONException {

    	BatteryModeCell batteryModeEntity = null;
        if (!TextUtils.isEmpty(powerPlanJsonData)) {
            JSONObject powerPlanJson = new JSONObject(powerPlanJsonData);
            batteryModeEntity = new BatteryModeCell();
            batteryModeEntity.id = powerPlanJson.optInt(BatteryModeInfoKey.ID);
            batteryModeEntity.modeName = powerPlanJson.optString(BatteryModeInfoKey.MODE_NAME);
            batteryModeEntity.permission = powerPlanJson.optInt(BatteryModeInfoKey.PERMISSION);
            batteryModeEntity.selectStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.SELECT_STATUS);
            batteryModeEntity.editStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.EDIT_STATUS);
            batteryModeEntity.displayStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.DISPLAY_STATUS);
            
            batteryModeEntity.airPlanModeStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.AIR_PLAN_MODE);
            batteryModeEntity.mobileDataStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.MOBILE_DATA);
            batteryModeEntity.wifiStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.WIFI);
            batteryModeEntity.bluetoothStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.BLUETOOTH);
            batteryModeEntity.vibrateStatus = powerPlanJson.optBoolean(BatteryModeInfoKey.VIBRATE);
            
            batteryModeEntity.screenTimeoutStatus = powerPlanJson.optInt(BatteryModeInfoKey.SCREEN_TIMEOUT_STATUS);
            batteryModeEntity.brightnessStatus = powerPlanJson.optInt(BatteryModeInfoKey.BRIGHTNESS);
            batteryModeEntity.ringerVolume = powerPlanJson.optInt(BatteryModeInfoKey.RINGER_VOLUME);
        }
        return batteryModeEntity;

    }


}









