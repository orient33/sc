package com.sudoteam.securitycenter.checkitem;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.SmsApplication;
import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.CheckResult.IFix;
import com.sudoteam.securitycenter.ICheck;

public class DefaultSmsCheck implements ICheck,IFix{
	
	private static final boolean DEBUG = true;
	private static final String TAG = "DefaultSmsCheck";
	
	private TelephonyManager mTm;
	private CheckResult mCheckResult; 
	
	private ArrayList<String> mRelySmsList;
	
	public DefaultSmsCheck(Context context){
		mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mRelySmsList = loadRelySms(context);
		initCheckResult(context);
	}
	
	@Override
	public CheckResult doCheck(Context context) {
		ComponentName appName = SmsApplication.getDefaultSmsApplication(context, true);
		if(appName != null){
			boolean isSysApp = isSystemApp(context, appName.getPackageName());
			if(!isSysApp){
				boolean found = false;
				for (String pkgName : mRelySmsList) {
					if(appName.getPackageName().equals(pkgName)){
						found = true;
						break;
					}
				}
				if(!found){
					mCheckResult.content = context.getResources().getString(R.string.default_sms_check_warning);
					mCheckResult.type = CheckResult.TYPE_MANUAL;
				}
			}
		}
		return mCheckResult;
	}
	
	@Override
	public boolean doFix(Context context) {
		Intent intent =  new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);  
		context.startActivity(intent);   
		return true;
	}
    
    public boolean isSmsSupported() {
        // Some tablet has sim card but could not do telephony operations. Skip those.
        return (mTm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE);
    }
    
    private void initCheckResult(Context context){
		mCheckResult = new CheckResult();
		mCheckResult.name = context.getResources().getString(R.string.default_sms_check_name);
		mCheckResult.content = context.getResources().getString(R.string.default_sms_check_pass);
		mCheckResult.type = CheckResult.TYPE_PASSED;
		mCheckResult.callback = this;
	}
    
    public boolean isSystemApp(Context context,String pkgName){
    	boolean isSysApp = false;
    	PackageManager pkgManager = context.getPackageManager();
		try {
			ApplicationInfo appInfo = pkgManager.getApplicationInfo(pkgName, 0);
			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 ){
				isSysApp = true;
			}
		} catch (NameNotFoundException e) {
			log_e("default sms NameNotFoundException...");
		}
    	return isSysApp;
    }
    
    private ArrayList<String> loadRelySms(Context context) {
    	ArrayList<String> smsList = new ArrayList<String>();
    	smsList.add("aa.bb.cc");
		return smsList;
	}
    
    private static void log_e(String msg) {
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

}






