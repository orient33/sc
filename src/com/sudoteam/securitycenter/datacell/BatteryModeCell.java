package com.sudoteam.securitycenter.datacell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.database.PowerManagerDbHelper;
import com.sudoteam.securitycenter.deviceutils.BrightnessSettings;
import com.sudoteam.securitycenter.deviceutils.DeviceBaseController;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;

/**
 * Created by huhuajun on 14-10-30.
 */
@Table(name = "BatteryModeCell")
public class BatteryModeCell implements Serializable{

	/**
	 * permission 权限（0不可删除的，1可删除）
	 */
	public static final int PERMISSION_DELETE_ENABLE = 1;
	public static final int PERMISSION_DELETE_REFUSE = 0;
	public static final String BATTERY_MODE_ENTITY = "power_plan_entity";
	public static final int[] DEFAULT_SCREEN_TIME_STATUS = new int[]{15,60,300,1800};
	public static final int[] DEFAULT_SCREEN_TIMEOUT_RES_ID = new int[]{R.drawable.power_screen_timeout_15s,
																		 R.drawable.power_screen_timeout_30s,
																		 R.drawable.power_screen_timeout_5m,
																		 R.drawable.power_screen_timeout_30m};
	
	public static final int[] DEFAULT_BRIGHTNESS_STATUS = new int[]{1,65,130,195,254};
	public static final int[] DEFAULT_BRIGHTNESS_RES_ID = new int[]{R.drawable.power_brightness_auto,
																	R.drawable.power_brightness_41,
																	R.drawable.power_brightness_42,
																	R.drawable.power_brightness_43,
																	R.drawable.power_brightness_44};
	
	@Column(column = "id")
	@NoAutoIncrement
	public int id;
	@Column(column = "modeName")
	public String modeName;
	@Column(column = "permission")
	public int permission = 0;
	@Column(column = "selectStatus")
	public boolean selectStatus ;
	@Column(column = "editStatus")
	public boolean editStatus ;
	@Column(column = "displayStatus")
	public boolean displayStatus ;

	@Column(column = "airPlanModeStatus")
    public boolean airPlanModeStatus;
	@Column(column = "mobileDataStatus")
    public boolean mobileDataStatus;//移动数据
	@Column(column = "wifiStatus")
    public boolean wifiStatus;
	@Column(column = "bluetoothStatus")
    public boolean bluetoothStatus;
	@Column(column = "vibrate")
	public boolean vibrateStatus;//来电铃振动

	@Column(column = "ringerVolume")
	public int ringerVolume;//来电铃声音量(响铃>0，静音=0)
	@Column(column = "brightnessStatus")
    public int brightnessStatus;//屏幕亮度(1(自动),65,130,195,254)
	@Column(column = "screenTimeoutStatus")
	public int screenTimeoutStatus;//超时锁屏(15s,1m,5m,30m)
    
    public void excute(Context context){
    	DeviceBaseController.setAirPlanModeEnabled(context, airPlanModeStatus);
    	DeviceBaseController.setMobileDataEnabled(context, mobileDataStatus);
    	DeviceBaseController.setWifiEnabled(context, wifiStatus);
    	DeviceBaseController.setBluetoothEnabled(context, bluetoothStatus);
    	DeviceBaseController.setVirateWhenRinging(context, vibrateStatus);
    	DeviceBaseController.setRingerVolume(context, ringerVolume);
    	
    	setBrightness(context, brightnessStatus);
    	setScreenTimeout(context, screenTimeoutStatus);
    }
    
    public static BatteryModeCell createNewBatteryMode(Context context) {
		BatteryModeCell currentPowerPlan = new BatteryModeCell();
		currentPowerPlan.id = createUniqueId();
		currentPowerPlan.modeName = context.getResources().getString(R.string.battery_defined_plan_mode);
		currentPowerPlan.permission = BatteryModeCell.PERMISSION_DELETE_ENABLE;
		currentPowerPlan.selectStatus = false;
		currentPowerPlan.editStatus = true;
		currentPowerPlan.displayStatus = true;

		currentPowerPlan.airPlanModeStatus = DeviceBaseController.isAirplaneModeOn(context);
		currentPowerPlan.mobileDataStatus = DeviceBaseController.isMobileDataOn(context);
		currentPowerPlan.wifiStatus = DeviceBaseController.isWifiOn(context);
		currentPowerPlan.bluetoothStatus = DeviceBaseController.isBluetoothOn();
		currentPowerPlan.vibrateStatus = DeviceBaseController.isVirateWhenRingingOn(context);
		currentPowerPlan.ringerVolume = DeviceBaseController.getCurrentTypeVolume(context, AudioManager.STREAM_RING);

		currentPowerPlan.brightnessStatus = DEFAULT_BRIGHTNESS_STATUS[0];
		currentPowerPlan.screenTimeoutStatus = DEFAULT_SCREEN_TIME_STATUS[0];
		return currentPowerPlan;
	}
    
    public void insertSelfToDb(Context context){
    	PowerManagerDbHelper<BatteryModeCell> powerPlanDbMgr = new PowerManagerDbHelper<BatteryModeCell>(context);
    	powerPlanDbMgr.insert(this);
    }
    
    public void deleteSelfToDb(Context context){
    	PowerManagerDbHelper<BatteryModeCell> powerPlanDbMgr = new PowerManagerDbHelper<BatteryModeCell>(context);
    	powerPlanDbMgr.delete(this);
    }
    
    public void updateSelfToDb(Context context){
    	PowerManagerDbHelper<BatteryModeCell> powerPlanDbMgr = new PowerManagerDbHelper<BatteryModeCell>(context);
    	powerPlanDbMgr.update(this);
    }
    
    public void querySelfToDb(Context context){
    	PowerManagerDbHelper<BatteryModeCell> powerPlanDbMgr = new PowerManagerDbHelper<BatteryModeCell>(context);
    	powerPlanDbMgr.queryById(BatteryModeCell.class, id);
    }
    
    public static ArrayList<BatteryModeCell> queryAllToDb(Context context){
    	PowerManagerDbHelper<BatteryModeCell> powerPlanDbMgr = new PowerManagerDbHelper<BatteryModeCell>(context);
    	ArrayList<BatteryModeCell> planArrList = null;
    	List<BatteryModeCell> planList = powerPlanDbMgr.queryAll(BatteryModeCell.class);
    	if(planList != null){
    		planArrList = new ArrayList<BatteryModeCell>();
    		for (BatteryModeCell powerPlanCell : planList) {
    			planArrList.add(powerPlanCell);
    		}
    	}
    	return planArrList;
    }
    
    public void setBrightness(Context context,int brightnessStatus){
    	BrightnessSettings brightnessSetting = BrightnessSettings.getInstance(context);
    	for(int i=0;i<DEFAULT_BRIGHTNESS_STATUS.length;i++){
    		if(DEFAULT_BRIGHTNESS_STATUS[i] == brightnessStatus){
    			if(brightnessStatus == 1){
    				brightnessSetting.setBrightnessMode(brightnessStatus);
    			}else{
    				brightnessSetting.setBrightnessMode(0);
    				brightnessSetting.setSysScreenBrightness(brightnessStatus);
    				brightnessSetting.setBrightness(brightnessStatus);
    				brightnessSetting.setActScreenBrightness(brightnessStatus);
    			}
    			return;
    		}
    	}
    	brightnessSetting.setBrightnessMode(DEFAULT_BRIGHTNESS_STATUS[0]);
    }
    
    public void setScreenTimeout(Context context,int timeoutStatus){
    	for(int i=0;i<DEFAULT_SCREEN_TIME_STATUS.length;i++){
    		if(DEFAULT_SCREEN_TIME_STATUS[i] == timeoutStatus){
    			DeviceBaseController.setLockedScreenTimeout(context, DEFAULT_SCREEN_TIME_STATUS[i]);
    			return;
    		}
    	}
    	DeviceBaseController.setLockedScreenTimeout(context, DEFAULT_SCREEN_TIME_STATUS[0]);
    }
    
    public static int createUniqueId(){
    	Long uniqueId = System.currentTimeMillis();
    	return Math.abs(uniqueId.hashCode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || !(obj instanceof BatteryModeCell)){
            return false;
        }
        BatteryModeCell batteryModeInfo = (BatteryModeCell) obj;
        return id == batteryModeInfo.id ? true :false;
    }
    
    @Override
    public String toString() {
    	return "PowerPlanCell{" +
                "id=" + id +
                ", modeName='" + modeName + '\'' +
                ", permission=" + permission +
                ", airPlanModeStatus=" + airPlanModeStatus +
                ", mobileDataStatus=" + mobileDataStatus +
                ", wifiStatus=" + wifiStatus +
                ", bluetoothStatus=" + bluetoothStatus +
                ", lockedScreenTimeout=" + screenTimeoutStatus +
                ", brightness=" + brightnessStatus +
                ", ringerVolume=" + ringerVolume +
                '}';
    }


}
