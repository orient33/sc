package com.sudoteam.securitycenter.deviceutils;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.activity.PowerManagerActivity;
import com.sudoteam.securitycenter.database.DbOperateType;
import com.sudoteam.securitycenter.datacell.BatteryModeCell;

public class BatteryModeController implements OnClickListener {

	private static final boolean DEBUG = true;
	private static final String TAG = "BatteryModeController";

	public static final int NOTHING = 0;
	public static final int TOGGLE_STATUS_CHANGE = 1;
	public static final int DISPLAY_STATUS_CHANGE = 2;
	
	private static BatteryModeCell originalBatteryMode;

	private RelativeLayout mBatteryModeTopLayout;
	private TextView mBatteryModeNameTv;
	private ToggleButton mBatteryModeToggle;
	private ImageView mBatteryModeDelIv;
	private EditText mBatteryModeEdit;
	private LinearLayout mBatteryModeSwitchLayout;

	private TextView mAirPlanModeTv;
	private TextView mMobileDataTv;
	private TextView mWifiTv;
	private TextView mBluetoothTv;
	private TextView mBrightnessTv;
	private TextView mRingerTv;
	private TextView mVibrateTv;
	private TextView mScreenTimeoutTv;

	private Context mContext;
	private BatteryModeCell mBatteryModeInfo;
	private DataChangeListener mDataChageCallback;
	private BroadcastReceiver mReceiver;

	public BatteryModeController(Context context, ArrayList<View> childViews,
			BatteryModeCell batteryModeInfo,
			DataChangeListener dataChageCallback) {
		mContext = context;
		mBatteryModeInfo = batteryModeInfo;
		mDataChageCallback = dataChageCallback;
		mReceiver = new BatteryModeReceiver();
		if (batteryModeInfo.editStatus) {
			mContext.registerReceiver(mReceiver, new IntentFilter(
					PowerManagerActivity.SAVE_BATTERY_MODE));
		}
		mBatteryModeTopLayout = (RelativeLayout) childViews.get(0);
		mBatteryModeNameTv = (TextView) childViews.get(1);
		mBatteryModeToggle = (ToggleButton) childViews.get(2);
		mBatteryModeDelIv = (ImageView) childViews.get(3);
		mBatteryModeEdit = (EditText) childViews.get(4);

		mBatteryModeSwitchLayout = (LinearLayout) childViews.get(5);
		mAirPlanModeTv = (TextView) childViews.get(6);
		mMobileDataTv = (TextView) childViews.get(7);
		mWifiTv = (TextView) childViews.get(8);
		mBluetoothTv = (TextView) childViews.get(9);
		mScreenTimeoutTv = (TextView) childViews.get(10);
		mBrightnessTv = (TextView) childViews.get(11);
		mRingerTv = (TextView) childViews.get(12);
		mVibrateTv = (TextView) childViews.get(13);
		
		if(originalBatteryMode == null){
			originalBatteryMode = BatteryModeCell.createNewBatteryMode(context);
		}
	}

	public void controller() {
		onBoundData();
		onBoundEvent();
	}

	private void onBoundData() {
		if (mBatteryModeInfo.editStatus) {
			mBatteryModeTopLayout.setVisibility(View.INVISIBLE);
			mBatteryModeEdit.setVisibility(View.VISIBLE);
		} else {
			mBatteryModeTopLayout.setVisibility(View.VISIBLE);
			mBatteryModeEdit.setVisibility(View.INVISIBLE);
		}

		mBatteryModeTopLayout.setTag(mBatteryModeInfo.displayStatus);
		if (mBatteryModeInfo.displayStatus) {
			mBatteryModeSwitchLayout.setVisibility(View.VISIBLE);
		} else {
			mBatteryModeSwitchLayout.setVisibility(View.GONE);
		}

		mBatteryModeNameTv.setText(mBatteryModeInfo.modeName);
		mBatteryModeToggle.setChecked(mBatteryModeInfo.selectStatus);
		mBatteryModeToggle.setTag(mBatteryModeInfo.selectStatus);

		if (mBatteryModeInfo.permission == BatteryModeCell.PERMISSION_DELETE_ENABLE) {
			mBatteryModeDelIv.setVisibility(View.VISIBLE);
		} else {
			mBatteryModeDelIv.setVisibility(View.GONE);
		}

		initSwitchDrawable(
				mAirPlanModeTv,
				mBatteryModeInfo.airPlanModeStatus,
				mBatteryModeInfo.airPlanModeStatus ? R.drawable.power_air_plan_mode_open
						: R.drawable.power_air_plan_mode_close,
				mBatteryModeInfo.airPlanModeStatus ? R.color.battery_switcher_checked_color
						: R.color.battery_switcher_unchecked_color);
		initSwitchDrawable(
				mMobileDataTv,
				mBatteryModeInfo.mobileDataStatus,
				mBatteryModeInfo.mobileDataStatus ? R.drawable.power_mobile_data_open
						: R.drawable.power_mobile_data_close,
				mBatteryModeInfo.mobileDataStatus ? R.color.battery_switcher_checked_color
						: R.color.battery_switcher_unchecked_color);
		initSwitchDrawable(
				mWifiTv,
				mBatteryModeInfo.wifiStatus,
				mBatteryModeInfo.wifiStatus ? R.drawable.power_wifi_open
						: R.drawable.power_wifi_close,
				mBatteryModeInfo.wifiStatus ? R.color.battery_switcher_checked_color
						: R.color.battery_switcher_unchecked_color);
		initSwitchDrawable(
				mBluetoothTv,
				mBatteryModeInfo.bluetoothStatus,
				mBatteryModeInfo.bluetoothStatus ? R.drawable.power_bluetooth_open
						: R.drawable.power_bluetooth_close,
				mBatteryModeInfo.bluetoothStatus ? R.color.battery_switcher_checked_color
						: R.color.battery_switcher_unchecked_color);
		initSwitchDrawable(
				mVibrateTv,
				mBatteryModeInfo.vibrateStatus,
				mBatteryModeInfo.vibrateStatus ? R.drawable.power_vibrate_open
						: R.drawable.power_vibrate_close,
				mBatteryModeInfo.vibrateStatus ? R.color.battery_switcher_checked_color
						: R.color.battery_switcher_unchecked_color);
		initSwitchDrawable(
				mRingerTv,
				mBatteryModeInfo.ringerVolume,
				(mBatteryModeInfo.ringerVolume > 0) ? R.drawable.power_ringer_open
						: R.drawable.power_ringer_close,
				(mBatteryModeInfo.ringerVolume > 0) ? R.color.battery_switcher_checked_color
						: R.color.battery_switcher_unchecked_color);

		initBrightness(mBatteryModeInfo.brightnessStatus);
		initScreenTimeout(mBatteryModeInfo.screenTimeoutStatus);
	}

	/**
	 * 初始化亮度状态
	 * 
	 * @param brightness
	 */
	private void initBrightness(int brightness) {
		int statusIndex = BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS[0];
		for (int i = 0; i < BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS.length; i++) {
			if (brightness == BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS[i]) {
				statusIndex = i;
				break;
			}
		}
		initSwitchDrawable(mBrightnessTv,
				BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS[statusIndex],
				BatteryModeCell.DEFAULT_BRIGHTNESS_RES_ID[statusIndex],
				R.color.battery_switcher_checked_color);
	}

	/**
	 * 初始化灭屏状态
	 * 
	 * @param timeoutStatus
	 */
	private void initScreenTimeout(int timeoutStatus) {
		int statusIndex = BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS[0];
		for (int i = 0; i < BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS.length; i++) {
			if (timeoutStatus == BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS[i]) {
				statusIndex = i;
				break;
			}
		}
		initSwitchDrawable(mScreenTimeoutTv,
				BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS[statusIndex],
				BatteryModeCell.DEFAULT_SCREEN_TIMEOUT_RES_ID[statusIndex],
				R.color.battery_switcher_checked_color);
	}

	private void initSwitchDrawable(TextView textView, Object status,
			int resDrawable, int resTextColor) {
		textView.setTag(status);
		Drawable switcherDrawable = mContext.getResources().getDrawable(
				resDrawable);
		switcherDrawable.setBounds(0, 0, switcherDrawable.getMinimumWidth(),
				switcherDrawable.getMinimumHeight());// 必须设置图片大小，否则不显示
		textView.setCompoundDrawables(null, switcherDrawable, null, null);
		textView.setTextColor(mContext.getResources().getColor(resTextColor));
	}

	private void onBoundEvent() {
		mAirPlanModeTv.setOnClickListener(this);
		mMobileDataTv.setOnClickListener(this);
		mWifiTv.setOnClickListener(this);
		mBluetoothTv.setOnClickListener(this);
		mBrightnessTv.setOnClickListener(this);
		mRingerTv.setOnClickListener(this);
		mVibrateTv.setOnClickListener(this);
		mScreenTimeoutTv.setOnClickListener(this);

		mBatteryModeTopLayout.setOnClickListener(this);

		mBatteryModeToggle.setOnClickListener(this);
		mBatteryModeDelIv.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Object oldStatus = v.getTag();
		if (v.getId() == mAirPlanModeTv.getId()) {
			boolean updateStatus = !((Boolean) oldStatus);
			initSwitchDrawable(mAirPlanModeTv, updateStatus,
					updateStatus ? R.drawable.power_air_plan_mode_open
							: R.drawable.power_air_plan_mode_close,
					updateStatus ? R.color.battery_switcher_checked_color
							: R.color.battery_switcher_unchecked_color);
		} else if (v.getId() == mMobileDataTv.getId()) {
			boolean updateStatus = !((Boolean) oldStatus);
			initSwitchDrawable(mMobileDataTv, updateStatus,
					updateStatus ? R.drawable.power_mobile_data_open
							: R.drawable.power_mobile_data_close,
					updateStatus ? R.color.battery_switcher_checked_color
							: R.color.battery_switcher_unchecked_color);

		} else if (v.getId() == mWifiTv.getId()) {
			boolean updateStatus = !((Boolean) oldStatus);
			initSwitchDrawable(mWifiTv, updateStatus,
					updateStatus ? R.drawable.power_wifi_open
							: R.drawable.power_wifi_close,
					updateStatus ? R.color.battery_switcher_checked_color
							: R.color.battery_switcher_unchecked_color);

		} else if (v.getId() == mBluetoothTv.getId()) {
			boolean updateStatus = !((Boolean) oldStatus);
			initSwitchDrawable(mBluetoothTv, updateStatus,
					updateStatus ? R.drawable.power_bluetooth_open
							: R.drawable.power_bluetooth_close,
					updateStatus ? R.color.battery_switcher_checked_color
							: R.color.battery_switcher_unchecked_color);

		} else if (v.getId() == mVibrateTv.getId()) {
			boolean updateStatus = !((Boolean) oldStatus);
			initSwitchDrawable(mVibrateTv, updateStatus,
					updateStatus ? R.drawable.power_vibrate_open
							: R.drawable.power_vibrate_close,
					updateStatus ? R.color.battery_switcher_checked_color
							: R.color.battery_switcher_unchecked_color);
		} else if (v.getId() == mRingerTv.getId()) {
			int updateStatus = ((Integer) oldStatus) > 0 ? 0
					: DeviceBaseController.getCurrentTypeMaxVolume(mContext,
							AudioManager.STREAM_RING) - 2
			/*
			 * DeviceBaseController.getCurrentTypeVolume(mContext,AudioManager.
			 * STREAM_RING)
			 */;
			initSwitchDrawable(mRingerTv, updateStatus,
					updateStatus > 0 ? R.drawable.power_ringer_open
							: R.drawable.power_ringer_close,
					updateStatus > 0 ? R.color.battery_switcher_checked_color
							: R.color.battery_switcher_unchecked_color);
		} else if (v.getId() == mScreenTimeoutTv.getId()) {
			screenTimeoutStatusChange((Integer) oldStatus);
		} else if (v.getId() == mBrightnessTv.getId()) {
			brightnessStatusChange((Integer) oldStatus);
		} else if (v.getId() == mBatteryModeTopLayout.getId()) {
			displayStatusChange((Boolean) oldStatus);
			return;
		} else if (v.getId() == mBatteryModeToggle.getId()) {
			toggleStatusChange();
			return;
		} else if (v.getId() == mBatteryModeDelIv.getId()) {
			deleteBatteryMode();
			return;
		}

		mBatteryModeInfo = getUpdateBatteryMode();
		mBatteryModeInfo.updateSelfToDb(mContext);
		mDataChageCallback.dataChange(mBatteryModeInfo,
				DbOperateType.DB_UPDATE, NOTHING);

	}

	private void screenTimeoutStatusChange(int currentStatus) {
		int nextStatusIndex = 0;
		for (int i = 0; i < BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS.length; i++) {
			if (currentStatus == BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS[i]) {
				if (i == BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS.length - 1) {
					nextStatusIndex = 0;
				} else {
					nextStatusIndex = i + 1;
				}
				break;
			}
		}
		initSwitchDrawable(mScreenTimeoutTv,
				BatteryModeCell.DEFAULT_SCREEN_TIME_STATUS[nextStatusIndex],
				BatteryModeCell.DEFAULT_SCREEN_TIMEOUT_RES_ID[nextStatusIndex],
				R.color.battery_switcher_checked_color);

	}

	private void brightnessStatusChange(int currentStatus) {
		int nextStatusIndex = 0;
		for (int i = 0; i < BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS.length; i++) {
			if (currentStatus == BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS[i]) {
				if (i == BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS.length - 1) {
					nextStatusIndex = 0;
				} else {
					nextStatusIndex = i + 1;
				}
				break;
			}
		}
		initSwitchDrawable(mBrightnessTv,
				BatteryModeCell.DEFAULT_BRIGHTNESS_STATUS[nextStatusIndex],
				BatteryModeCell.DEFAULT_BRIGHTNESS_RES_ID[nextStatusIndex],
				R.color.battery_switcher_checked_color);
	}

	private void displayStatusChange(boolean currentStatus) {
		BatteryModeCell batteryModeInfo = getUpdateBatteryMode();
		if (currentStatus) {
			batteryModeInfo.displayStatus = false;
		} else {
			batteryModeInfo.displayStatus = true;
		}
		batteryModeInfo.updateSelfToDb(mContext);
		mDataChageCallback.dataChange(batteryModeInfo, DbOperateType.DB_UPDATE,
				DISPLAY_STATUS_CHANGE);
	}

	private void toggleStatusChange() {
		BatteryModeCell batteryModeInfo = getUpdateBatteryMode();
		batteryModeInfo.selectStatus = mBatteryModeToggle.isChecked();
		batteryModeInfo.updateSelfToDb(mContext);
		mDataChageCallback.dataChange(batteryModeInfo, DbOperateType.DB_UPDATE,
				TOGGLE_STATUS_CHANGE);
		if (batteryModeInfo.selectStatus) {
			batteryModeInfo.excute(mContext);
		}else{
			//ok??
			if(originalBatteryMode != null){
				originalBatteryMode.excute(mContext);
			}
		}
	}

	private void deleteBatteryMode() {
		BatteryModeCell batteryModeInfo = getUpdateBatteryMode();
		batteryModeInfo.deleteSelfToDb(mContext);
		mDataChageCallback.dataChange(batteryModeInfo, DbOperateType.DB_DELETE,
				NOTHING);
	}

	public BatteryModeCell getUpdateBatteryMode() {
		if (mBatteryModeInfo.editStatus) {
			String modeName = mBatteryModeEdit.getText().toString();
			if (!TextUtils.isEmpty(modeName)) {
				mBatteryModeInfo.modeName = modeName;
			}
		}
		mBatteryModeInfo.selectStatus = mBatteryModeToggle.isChecked();
		mBatteryModeInfo.airPlanModeStatus = (Boolean) mAirPlanModeTv.getTag();
		mBatteryModeInfo.mobileDataStatus = (Boolean) mMobileDataTv.getTag();
		mBatteryModeInfo.wifiStatus = (Boolean) mWifiTv.getTag();
		mBatteryModeInfo.bluetoothStatus = (Boolean) mBluetoothTv.getTag();
		mBatteryModeInfo.vibrateStatus = (Boolean) mVibrateTv.getTag();
		mBatteryModeInfo.ringerVolume = (Integer) mRingerTv.getTag();
		mBatteryModeInfo.brightnessStatus = (Integer) mBrightnessTv.getTag();
		mBatteryModeInfo.screenTimeoutStatus = (Integer) mScreenTimeoutTv
				.getTag();
		return mBatteryModeInfo;
	}

	public interface DataChangeListener {
		/**
		 * 
		 * @param batteryModeInfo
		 * @param operate
		 * @param scanFlag
		 *            (0(nothing),toggle(1),2(displaystatus))
		 */
		public void dataChange(BatteryModeCell batteryModeInfo,
				DbOperateType operate, int scanFlag);
	}

	public class BatteryModeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			log_e("BatteryModeReceiver:" + action);
			if (action.equals(PowerManagerActivity.SAVE_BATTERY_MODE)) {
				BatteryModeCell batteryModeInfo = getUpdateBatteryMode();
				if (batteryModeInfo.editStatus) {
					batteryModeInfo.editStatus = false;
					batteryModeInfo.insertSelfToDb(mContext);
					mDataChageCallback.dataChange(batteryModeInfo,
							DbOperateType.DB_UPDATE, NOTHING);
				}
				mContext.unregisterReceiver(mReceiver);
			}
		}
	}

	private static void log_e(String msg) {
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}
