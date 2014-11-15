package com.sudoteam.securitycenter.checkitem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.text.TextUtils;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.ICheck;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class RootCheck implements ICheck {

    @Override
    public CheckResult doCheck(Context context) {
        final CheckResult cr = new CheckResult();
        final boolean isRoot = isRooted(context);
        cr.name = context.getString(R.string.isRoot);
        if (isRoot) {
            cr.type = CheckResult.TYPE_CANNO_FIX;
            cr.content = context.getString(R.string.check_rooted);
        } else {
            cr.type = CheckResult.TYPE_PASSED;
            cr.content = context.getString(R.string.check_unroot);
        }
        return cr;
    }

    /**
     * 查询是否root了
     */
    private static boolean isRooted(Context c) {
        try {
            String result = execShell("type su"); // eg : su is tracked alias for /system/xbin/su
            String su_path = result.substring(result.lastIndexOf(" ")); // eg : /system/xbin/su
            result = execShell("md5  " + su_path);
            String md5 = result.substring(0, result.indexOf(" "));
            Util.i("md5 su : " + md5);
            boolean noRoot = TextUtils.isEmpty(md5);
            return !noRoot;
        } catch (Exception e) {
            Util.e("[RootCheck] " + e);
        }
        return false;
    }

    private static String execShell(String cmd) {
        String[] cmdStrings = new String[] { "sh", "-c", cmd };
        Runtime run = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder("");
        try {
            Process proc = run.exec(cmdStrings);
            InputStream in = proc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            if (proc.waitFor() != 0) {
            }
        } catch (Exception e) {
            Util.e("[RootCheck] " + e);
        }
        return sb.toString();
    }
}