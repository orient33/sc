package com.sudoteam.securitycenter.mac;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.mac.AppListFragment.OneApp;
import com.sudoteam.securitycenter.mac.AppListFragment.OpMode;
import com.sudoteam.securitycenter.mac.OpsListFragment.OneOp;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MacUtil {
    private  static final boolean DEBUG = false;
    private static List<String> MODE_LABEL = new ArrayList<String>();

    // title is coarse, eg : for op 0, 1, title is same -- Location, but summary is coarse location,fine location.
    // so use summary as a op's title/name.
    private static ArrayList<String> OPS_TITLE = new ArrayList<String>(AppOpsManager._NUM_OP);
    static String getLabelForOp(Context c, int op) {
        if (OPS_TITLE.size() == 0) {
            String summary[] = c.getResources().getStringArray(R.array.app_ops_labels_cm);
            for (String s : summary)
                OPS_TITLE.add(s);
        }
        return OPS_TITLE.get(op);
    }

    static String getLabelForMode(Context c, int mode) {
        if (MODE_LABEL.size() == 0) {
            String[] str = c.getResources().getStringArray(R.array.app_ops_permissions);
            for (String s : str)
                MODE_LABEL.add(s);
        }
        return MODE_LABEL.get(mode);
    }


    static List<OneApp> getAppsInfo(Context act, AppOpsManager aom) {
        final PackageManager pm = act.getPackageManager();
        List<OneApp> list = new ArrayList<OneApp>();
        List<ApplicationInfo> ais = pm.getInstalledApplications(0);
        if (ais == null) return list;


        // find all permission in AppOpsManager and the index for this permission
        final ArrayList<String> perms = new ArrayList<String>();
        final ArrayList<Integer> permOps = new ArrayList<Integer>();
        for (int op = 0; op < AppOpsManager._NUM_OP; ++op) {
            String perm = AppOpsManager.opToPermission(op);
            if (perm != null && !perms.contains(perm)) {
                perms.add(perm);
                permOps.add(op);
            }
        }

        for (ApplicationInfo ai : ais) {
            if (0 != (ai.flags & ApplicationInfo.FLAG_SYSTEM))
                continue;
            Drawable icon = Util.getDrawableForPackage(pm, ai.packageName);
            String name = Util.getNameForPackage(pm, ai.packageName);

            i("title : " + name + ", packageName : " + ai.packageName);
            // 1 get this application's ops from AppOpsManager.getOpsForPackage(uid,packageName,null);
            i("1 get ops -------from AppOpsManager-----------------------");
            List<AppOpsManager.PackageOps> ll = aom.getOpsForPackage(ai.uid, ai.packageName, null);
            if (ll != null && DEBUG) {   // this block just for debug.
                if (ll.size() != 1)
                    throw new RuntimeException("one app has more than 1 package name???");
                AppOpsManager.PackageOps po = ll.get(0);
                for (AppOpsManager.OpEntry oe : po.getOps()) {
                    i("OpEntry : " + OpEntry2String(oe));
                }
            }

            OneApp oa = new OneApp(ai.uid, name, ai.packageName, icon, ll == null ? null : ll.get(0).getOps());

            // 2 check ops depend on this application's permission
            i("2 from PackageManager, get op depend on permissions. ");
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo(ai.packageName, PackageManager.GET_PERMISSIONS);
            } catch (PackageManager.NameNotFoundException e) {
                Util.e("" + e);
            }
            if (pi == null || pi.requestedPermissions == null) continue;
            for (int i = 0; i < pi.requestedPermissions.length; ++i) {
                if (pi.requestedPermissionsFlags != null) {
                    if ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0)
                        continue;   //this permission is not granted.
                    for (int k = 0; k < perms.size(); ++k) {
                        if (!perms.get(k).equals(pi.requestedPermissions[i]))
                            continue;
                        int op = permOps.get(k);    // get this op code.
                        //add op to the app
                        if (oa.addOp(op, aom.checkOpNoThrow(op, ai.uid, ai.packageName)))
                            ;//i(" add op :" + AppOpsManager.opToName(op));
                    }
                }
            }
            i("**************************************************************************");
            oa.buildOpSwitch();
            list.add(oa);
        }
        return list;
    }

    static OneApp getAppInfo(Context act, AppOpsManager aom, String pkgName) {
        List<OneApp> list = getAppsInfo(act, aom);
        for (OneApp oa : list)
            if (oa.pkgName.equals(pkgName))
                return oa;
        return null;
    }

    static String OpEntry2String(AppOpsManager.OpEntry oe) {
        return "op=" + oe.getOp() + ", " + AppOpsManager.opToName(oe.getOp()) + ", mode=" + oe.getMode() +
                ", time=" + oe.getTime();
    }

    static void i(String s) {
        if(DEBUG)
        Util.i("[MacUtil] " + s);
    }

    static List<OneOp> getOpsList(Context c, AppOpsManager aom) {
        List<OneOp> list = new ArrayList<OneOp>();
        List<OneApp> apps = getAppsInfo(c, aom);    // :TODO may be should cache this list.
        for (int op = 0, count; op < AppOpsManager._NUM_OP; ++op) {
            count = 0;
            final ArrayList<String> pkgs = new ArrayList<String>();
//            final ArrayList<Integer> modes = new ArrayList<Integer>();
            for (OneApp oa : apps) {
                for (OpMode oaop : oa.getOpsSwitches()) {
                    if (op == oaop.getOp()) {
                        ++count;
//                        modes.add(oaop.getMode());
                        pkgs.add(oa.pkgName);
                        break;
                    }
                }
            }
            if (count > 0) { // filter op which no application use
                OneOp oo = new OneOp(op, getLabelForOp(null, op), count, pkgs);
                list.add(oo);
            }
        }
        return list;
    }

    static OneOp getOpInfo(Context c, AppOpsManager aom, int op) {
        List<OneOp> list = getOpsList(c, aom);
        for (OneOp oo : list) {
            if (oo.op == op)
                return oo;
        }
        return null;
    }
    
    static void setMyMode(int code, int uid, String packageName, int mode,AppOpsManager aom){
        int now = aom.checkOp(code, uid, packageName);
        if(now == mode){
            Util.i("setMyMode " + packageName +",  but mode  is ALREADY  "+mode);
            return;
        }
        Util.i("setMyMode " + packageName +", mode ="+mode +" OK! ");
        aom.setMode(code, uid, packageName, mode);
    }
    
    private static final int MODE_ALLOWED = 0;
    private static final int MODE_IGNORED = 1;
    private static final int MODE_ASK = 2;


    static int modeToPosition(int mode) {
        switch (mode) {
            case AppOpsManager.MODE_ALLOWED:
                return MODE_ALLOWED;
            case AppOpsManager.MODE_IGNORED:
                return MODE_IGNORED;
            case AppOpsManager.MODE_ASK:
                return MODE_ASK;
        }
        ;

        return MODE_IGNORED;
    }

    static int positionToMode(int position) {
        switch (position) {
            case MODE_ALLOWED:
                return AppOpsManager.MODE_ALLOWED;
            case MODE_IGNORED:
                return AppOpsManager.MODE_IGNORED;
            case MODE_ASK:
                return AppOpsManager.MODE_ASK;
        }
        ;

        return AppOpsManager.MODE_IGNORED;
    }
}
