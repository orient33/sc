package com.sudoteam.securitycenter.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.database.DbOperateType;
import com.sudoteam.securitycenter.datacell.BatteryModeCell;
import com.sudoteam.securitycenter.deviceutils.BatteryModeController;
import com.sudoteam.securitycenter.deviceutils.BatteryModeController.DataChangeListener;

/**
 * Created by huhuajun on 14-10-30.
 */
public class BatteryModeAdapter extends AutoReuseViewAdapter {

	private static final boolean DEBUG = false;
	private static final String TAG = "BatteryModeAdapter";

	private ArrayList<BatteryModeCell> mListData;
	
    public BatteryModeAdapter(Context context,ArrayList<BatteryModeCell> listData){
        super(context,R.layout.power_manager_main_item,new int []{R.id.battery_plan_mode_top_layout,//0
        														  R.id.battery_plan_mode_name,//1
        														  R.id.battery_plan_status,//2
        														  R.id.battery_plan_mode_del,//3
        														  R.id.battery_mode_edit,//4
        														  R.id.device_switch_layout,//5
        														  R.id.air_plan_mode,//6
        														  R.id.mobile_data,//7
        														  R.id.wifi,//8
        														  R.id.bluetooth,//9
        														  R.id.locked_screen_timeout,//10
        														  R.id.brightness,//11
        														  R.id.ringer,//12
        														  R.id.vibrate });//13
        mListData = listData;
    }

    @Override
    public void onBoundDataAndEventToViews(final int position, View itemView, ArrayList<View> childViews) {
    	
    	BatteryModeCell currentBatteryModeInfo = mListData.get(position);
    	log_e("adapter  currentBatteryModeInfo position:"+position+"  "+currentBatteryModeInfo.selectStatus);
    	
    	new BatteryModeController(mContext, childViews, currentBatteryModeInfo, new DataChangeListener() {

			@Override
			public void dataChange(BatteryModeCell batteryModeInfo,DbOperateType operate,int scanFlag) {
				
				switch (operate) {
					case DB_DELETE:
						mListData.remove(position);
						break;
					case DB_UPDATE:
						if(scanFlag != 0){
							for (int i = 0; i < mListData.size(); i++) {
								if (scanFlag == BatteryModeController.DISPLAY_STATUS_CHANGE) {
									if(mListData.get(i).displayStatus && i != position){
										mListData.get(i).displayStatus = false;
									}
								}else if(scanFlag == BatteryModeController.TOGGLE_STATUS_CHANGE){
									if(mListData.get(i).selectStatus && i != position){
										mListData.get(i).selectStatus = false;
									}
								}
							}
						}
						log_e("adapter Toggle selectStatus:"+batteryModeInfo.selectStatus+" position:"+position);
						mListData.set(position, batteryModeInfo);
						break;
					default:
						break;
				}
				BatteryModeAdapter.this.notifyDataSetChanged();
			}
			
		}).controller();
    }
    
    private int searchBatteryModeByList(BatteryModeCell batteryModeInfo){
    	int batteryModeIndex = -1;
		for (int i = 0; i < mListData.size(); i++) {
			if(mListData.get(i).equals(batteryModeInfo)){
				batteryModeIndex = i;
				break;
			}
		}
		
		return batteryModeIndex;
    }
    
    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int i) {
        return mListData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    private static void log_e(String msg) {
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}
