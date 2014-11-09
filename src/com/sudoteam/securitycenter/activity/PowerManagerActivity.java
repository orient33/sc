package com.sudoteam.securitycenter.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.adapter.BatteryModeAdapter;
import com.sudoteam.securitycenter.constant.PowerManagerKey.BatteryModeInfoKey;
import com.sudoteam.securitycenter.datacell.BatteryChargeCell;
import com.sudoteam.securitycenter.datacell.BatteryInfoCell;
import com.sudoteam.securitycenter.datacell.BatteryModeCell;
import com.sudoteam.securitycenter.datacell.DataEntityFactory;
import com.sudoteam.securitycenter.deviceutils.BatteryInfoHelper;
import com.sudoteam.securitycenter.prefs.SystemPreference;
import com.sudoteam.securitycenter.service.BatteryChangeService;
import com.sudoteam.securitycenter.utils.ConvertHelper;

public class PowerManagerActivity extends Activity implements Handler.Callback {

	private static final boolean DEBUG = true;
	private static final String TAG = "PowerManagerActivity";
	
	public static final String SAVE_BATTERY_MODE = "powermanageractivity.save.batterymode";

	private static int sAcChargeSpeed = 0;
	private static int sUsbChargeSpeed = 0;
	private static int sDisChargeSpeed = 0;
	
	private static boolean isCreateBatteryMode = true;
	
	private ImageView mBatteryLogo;
	private TextView mBatteryStatusTimeSummary;
	private TextView mBatteryTimeHour;
	private TextView mBatteryTimeMinute;

	private Context mContext;
	private BatteryStatusReceiver mBatteryStatusReceiver;
	private Handler mUiHandler = new Handler(this);

	private ListView mBatteryModeList;
	private BaseAdapter mAdapter;
	private ArrayList<BatteryModeCell> mBatteryModeListData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.power_manager_main);
		registerBatteryStatusReceiver(this);
		mContext = this;
		addPowerManagerActionBar();
		sAcChargeSpeed = computeChargeSpeedByDb(this, BatteryManager.BATTERY_PLUGGED_AC);
		sUsbChargeSpeed = computeChargeSpeedByDb(this, BatteryManager.BATTERY_PLUGGED_USB);
		sDisChargeSpeed = computeChargeSpeedByDb(this, BatteryManager.BATTERY_STATUS_DISCHARGING);
		setUpViewsData();
		startBatteryHelperService(this);
		
	}
	
	private void registerBatteryStatusReceiver(Context context){
		mBatteryStatusReceiver = new BatteryStatusReceiver();
		context.registerReceiver(mBatteryStatusReceiver, new IntentFilter(BatteryInfoHelper.BATTERY_CHANGE_MSG));
	}
	
	private void startBatteryHelperService(Context context){
		Intent intent = new Intent(context, BatteryChangeService.class);
		context.startService(intent);
	}
	
	private void setUpViewsData() {

		mBatteryLogo = (ImageView) this.findViewById(R.id.battery_logo);
		mBatteryStatusTimeSummary = (TextView) this.findViewById(R.id.battery_status_time_summary);
		mBatteryTimeHour = (TextView) this.findViewById(R.id.battery_time_hour);
		mBatteryTimeMinute = (TextView) this.findViewById(R.id.battery_time_minute);
		mBatteryModeList = (ListView) this.findViewById(R.id.battery_plan_mode);
	
		mBatteryModeListData = loadLocalData(this);
		for (BatteryModeCell cell : mBatteryModeListData) {
			log_e("-----display status:"+cell.displayStatus);
		}
		mAdapter = new BatteryModeAdapter(this, mBatteryModeListData);
		mBatteryModeList.setAdapter(mAdapter);
		
	}
	
	private ArrayList<BatteryModeCell> loadLocalData(Context context){
    	ArrayList<BatteryModeCell> listData = null;
		boolean isFirstLaucher = SystemPreference.getInstance(context).isPowerManagerFirstLaunch();
		if(!isFirstLaucher){
			log_e("not first Laucher...");
			listData = readBatteryModeDataByDB(context);
			if(listData != null){
				return listData;
			}
		}
		SystemPreference.getInstance(context).setPowerManagerFirstLaunchFalse();
		String jsonData = readBatteryModeDataByAssets();
		try {
			if(!TextUtils.isEmpty(jsonData)){
				JSONObject powerPlanJo = new JSONObject(jsonData);
				JSONArray powerPlanJa = new JSONArray(powerPlanJo.optString(BatteryModeInfoKey.DEFAULT_BATTERY_MODE));
				listData = new ArrayList<BatteryModeCell>();
				for (int i = 0; i < powerPlanJa.length(); i++) {
					BatteryModeCell batteryModeInfo = DataEntityFactory.createBatteryModeEntity(powerPlanJa.optString(i));
					if(batteryModeInfo != null){
						listData.add(batteryModeInfo);
						batteryModeInfo.insertSelfToDb(context);
					}
				}
			}
		} catch (JSONException e) {
			log_e("loadLocalData JSONException...");
			listData = new ArrayList<BatteryModeCell>();
		}
		return listData; 
    }
    
    private String readBatteryModeDataByAssets(){
    	try {
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getResources().getAssets().open("power/power_default_battery_mode.json"),"utf-8"));
			StringBuilder sb = new StringBuilder();
			String line ;
			while((line = br.readLine())!= null){
				sb.append(line);
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			log_e("loadLocalData IOException:"+e.getMessage());
		}
    	return null;
    }
    
    private ArrayList<BatteryModeCell> readBatteryModeDataByDB(Context context){
    	ArrayList<BatteryModeCell> planArrList = null;
    	List<BatteryModeCell> planList = BatteryModeCell.queryAllToDb(context);
    	if(planList != null){
    		planArrList = new ArrayList<BatteryModeCell>();
    		for (BatteryModeCell powerPlanCell : planList) {
    			planArrList.add(powerPlanCell);
    		}
    	}
    	return planArrList;
    }

	private int computeChargeSpeedByDb(Context context,int chargeStatus) {
		int chargeSpeed = 0;
		ArrayList<BatteryChargeCell> batteryChargeList = BatteryChargeCell.queryAllToDb(context);
		if (batteryChargeList != null && batteryChargeList.size() > 0) {
			BatteryChargeCell batteryInfo = null;
			int chargeCount = 0;
			for (int i = 0; i < batteryChargeList.size(); i++) {
				batteryInfo = batteryChargeList.get(i);
				if (batteryInfo != null) {
					if (batteryInfo.chargeStatus == chargeStatus) {
						chargeSpeed += batteryInfo.chargeSpeed;
						chargeCount++;
					}
				}
			}
			if(chargeCount > 0){
				chargeSpeed = chargeSpeed / chargeCount;
			}
			log_e("computeChargeSpeedByDb batteryChargeList size:"+batteryChargeList.size()+"	chargeStatus:"+chargeStatus+" chargeSpeed:"+chargeSpeed);
		} 
		return chargeSpeed;
	}
	
	private void updateBatteryStatusUi(int batteryStatus,int hour,int minute) {
		boolean isCharging = (batteryStatus == BatteryManager.BATTERY_PLUGGED_AC ||
				batteryStatus == BatteryManager.BATTERY_PLUGGED_USB || 
				batteryStatus == BatteryManager.BATTERY_PLUGGED_WIRELESS) ?
			    true : false;
		mBatteryStatusTimeSummary.setText(mContext.getResources().getString( isCharging ? R.string.battery_status_charge_summary : R.string.battery_status_discharge_summary));
		mBatteryTimeHour.setText(hour+"");
		mBatteryTimeMinute.setText(minute+"");
		mBatteryLogo.setImageResource(isCharging ? R.drawable.power_battery_charging : R.drawable.power_battery_discharge);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == 1) {
			BatteryInfoCell batteryInfo = (BatteryInfoCell) msg.obj;
			int batteryStatus = batteryInfo.batteryStatus;
			int batteryTime = 0;
			if (batteryStatus == BatteryManager.BATTERY_PLUGGED_AC) {
				if (sAcChargeSpeed > 0) {
					batteryTime = (batteryInfo.batteryScale - batteryInfo.batteryLevel) * sAcChargeSpeed;
				} else {
					batteryTime = BatteryInfoHelper.getInstance().getDefaultAcChargeSurplusTime();
				}
			} else if (batteryStatus == BatteryManager.BATTERY_PLUGGED_USB) {
				if (sUsbChargeSpeed > 0) {
					batteryTime = (batteryInfo.batteryScale - batteryInfo.batteryLevel) * sUsbChargeSpeed;
				} else {
					batteryTime = BatteryInfoHelper.getInstance().getDefaultUsbChargeSurplusTime();
				}
			} else {
				if (sDisChargeSpeed > 0) {
					batteryTime = (int) ((batteryInfo.batteryLevel * sDisChargeSpeed));
				} else {
					batteryTime = BatteryInfoHelper.getInstance().getDefaultDisChargeSurplusTime();
				}
			}
			
			int hour = ConvertHelper.second2Hour(batteryTime);
			int minute = ConvertHelper.second2Minute(batteryTime);
			log_e("batteryStatus:"+batteryStatus+" hour:"+hour+" minute"+minute);
			updateBatteryStatusUi(batteryStatus, hour, minute);
		}
		return true;
	}
	
	public class BatteryStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			log_e( "activity onReceive action:" + action);
			if (action.equals(BatteryInfoHelper.BATTERY_CHANGE_MSG)) {
				BatteryInfoCell batteryInfo = (BatteryInfoCell) intent
						.getSerializableExtra(BatteryInfoHelper.BATTERY_CHANGE_KEY);
				Message batteryMsg = new Message();
				batteryMsg.what = 1;
				batteryMsg.obj = batteryInfo;
				mUiHandler.sendMessage(batteryMsg);
			}
		}
	}
	
	private void addPowerManagerActionBar(){
		String title = getString(R.string.module_save);
		Util.setActionBar(this,true,title,R.drawable.power_add_batterymode,new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onActionbarClick(v);
			}
		});
		
	}
	private void onActionbarClick(View v){
		synchronized (PowerManagerActivity.class) {
			final ImageView iv = (ImageView)v;
			if (isCreateBatteryMode) {
				isCreateBatteryMode = false;
				iv.setImageResource(R.drawable.power_save_batterymode);
				for (int i = 0; i < mBatteryModeListData.size(); i++) {
					if(mBatteryModeListData.get(i).displayStatus){
						mBatteryModeListData.get(i).displayStatus = false;
						mBatteryModeListData.get(i).updateSelfToDb(mContext);
					}
				}
				BatteryModeCell batteryModeInfo = BatteryModeCell.createNewBatteryMode(mContext);
				mBatteryModeListData.add(batteryModeInfo);
				new Thread(new Runnable() {
					@Override
					public void run() {
						mBatteryModeList.setSelection(mAdapter.getCount()-1);
					}
				}).start();
				mAdapter.notifyDataSetChanged();
			}else{
				iv.setImageResource(R.drawable.power_add_batterymode);
				mContext.sendBroadcast(new Intent(SAVE_BATTERY_MODE));
				isCreateBatteryMode = true;
			}
		}
	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mBatteryStatusReceiver);
	}

	private static void log_e(String msg){
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}











