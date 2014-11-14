package com.sudoteam.securitycenter.checkitem;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.provider.Settings;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.CheckResult.IFix;
import com.sudoteam.securitycenter.ICheck;

public class NonMarketAppsCheck implements ICheck, IFix {

	private CheckResult mCheckResult;

	public NonMarketAppsCheck() {
		initCheckResult();
	}

	@Override
	public CheckResult doCheck(Context context) {
		if (getNonMarketAppsEnabled(context)) {
			mCheckResult.content = "发现App未知来源打开";
			mCheckResult.type = CheckResult.TYPE_MANUAL;
			return mCheckResult;
		}
		return mCheckResult;
	}

	@Override
	public boolean doFix(Context context) {
		Intent intent = new Intent(
				Settings.ACTION_SECURITY_SETTINGS);
		context.startActivity(intent);
		return true;
	}

	private void initCheckResult() {
		mCheckResult = new CheckResult();
		mCheckResult.name = "App未知来源检测检查";
		mCheckResult.content = "App未知来源检测检查通过";
		mCheckResult.type = CheckResult.TYPE_PASSED;
		mCheckResult.callback = this;
	}

	public void setNonMarketAppsSwitchOn(Context context, boolean enabled) {
		final UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
		if (um.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)) {
			return;
		}
		// Change the system setting
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.INSTALL_NON_MARKET_APPS, enabled ? 1 : 0);
	}

	public boolean getNonMarketAppsEnabled(Context context) {
		return Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0;
	}

}
