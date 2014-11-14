package com.sudoteam.securitycenter.checkitem;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.CheckResult.IFix;
import com.sudoteam.securitycenter.ICheck;
import com.sudoteam.securitycenter.R;

public class AdbCheck implements ICheck,IFix{
	
	private CheckResult mCheckResult; 
	
	public AdbCheck(Context context){
		initCheckResult(context);
	}

	@Override
	public CheckResult doCheck(Context context) {
		if(getAdbEnabled(context)){
			mCheckResult.content = context.getResources().getString(R.string.adb_check_warning);
			mCheckResult.type = CheckResult.TYPE_MANUAL;
			return mCheckResult;
		}
		return mCheckResult;
	}

	@Override
	public boolean doFix(Context context) {
		Intent intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);  
		context.startActivity(intent);
		return true;
	}

	private void initCheckResult(Context context){
		mCheckResult = new CheckResult();
		mCheckResult.name = context.getResources().getString(R.string.adb_check_name);
		mCheckResult.content = context.getResources().getString(R.string.adb_check_pass);
		mCheckResult.type = CheckResult.TYPE_PASSED;
		mCheckResult.callback = this;
	}
	
	public void setAdbSwitchOn(Context context,boolean enabled){
		 Settings.Global.putInt(context.getContentResolver(),Settings.Global.ADB_ENABLED, enabled ? 1:0);
	}
	
	public boolean getAdbEnabled(Context context){
		return Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0;
	}
	

}




