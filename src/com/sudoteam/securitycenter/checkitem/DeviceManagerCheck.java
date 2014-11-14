package com.sudoteam.securitycenter.checkitem;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.CheckResult.IFix;
import com.sudoteam.securitycenter.ICheck;

import java.util.ArrayList;
import java.util.List;

public class DeviceManagerCheck implements ICheck, IFix {

    private static final boolean DEBUG = true;
    private static final String TAG = "DeviceManagerCheck";

    private static DeviceManagerCheck sInstance;

    private DevicePolicyManager mDPM;
    private CheckResult mCheckResult;

    private DeviceManagerCheck(Context context) {
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        initCheckResult();
    }

    public static DeviceManagerCheck getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DeviceManagerCheck.class) {
                if (sInstance == null) {
                    sInstance = new DeviceManagerCheck(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public CheckResult doCheck(Context context) {
        log_e("DeviceManagerCheck doCheck:" + mCheckResult.toString());
        List<ComponentName> cur = mDPM.getActiveAdmins();
        ArrayList<String> pkgNames = getNotSysApplications(context);
        if (pkgNames == null || cur == null) {
            return mCheckResult;
        }
        for (ComponentName componentName : cur) {
            log_e("active device componentName:" + componentName);
        }
        for (int i = 0; i < pkgNames.size(); i++) {
            for (ComponentName cn : cur) {
                if (cn.getPackageName().equals(pkgNames.get(i))) {
                    mCheckResult.content = "设备管理器检查到非系统应用使用";
                    mCheckResult.type = CheckResult.TYPE_MANUAL;
                    return mCheckResult;
                }
            }
        }
        return mCheckResult;
    }

    @Override
    public boolean doFix(Context context) {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        context.startActivity(intent);
        return true;
    }

    private void initCheckResult() {
        mCheckResult = new CheckResult();
        mCheckResult.name = "设备管理器检查";
        mCheckResult.content = "设备管理器通过";
        mCheckResult.type = CheckResult.TYPE_PASSED;
        mCheckResult.callback = this;
    }

    /**
     * Get all custom installed applications
     *
     * @param context
     * @return
     */
    public ArrayList<String> getNotSysApplications(Context context) {
        ArrayList<String> apps = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pkgList = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pkg = pkgList.get(i);
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pkg.packageName);
            }
        }
        return apps;
    }

    private static void log_e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }


}









