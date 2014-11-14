package com.sudoteam.securitycenter.checkitem;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.CheckResult.IFix;
import com.sudoteam.securitycenter.ICheck;

public class AdbCheck implements ICheck,IFix{
	
	private CheckResult mCheckResult; 
	
	public AdbCheck(){
		initCheckResult();
	}

	@Override
	public CheckResult doCheck(Context context) {
		if(getAdbEnabled(context)){
			mCheckResult.content = "发现USB调试打开";
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

	private void initCheckResult(){
		mCheckResult = new CheckResult();
		mCheckResult.name = "USB调试检查";
		mCheckResult.content = "USB调试检查通过";
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




