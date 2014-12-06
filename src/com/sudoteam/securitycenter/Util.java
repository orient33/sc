package com.sudoteam.securitycenter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.UserHandle;
import android.preference.PreferenceFrameLayout;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

public class Util {
    public static String TAG = "godin";
    private static HashMap<String, String> pkg2Name = new HashMap<String, String>();
    private static HashMap<String, Drawable> pkg2Drawable = new HashMap<String, Drawable>();


    public static int formatFileSize2MB(long size) {
        return (int) (size / 1024 / 1024);
    }

    public static ImageView setCustomTitle(final Activity act, boolean showBack, String t, int resId,View.OnClickListener clickSet){
        View v = setCustomTitle(act,showBack,t,clickSet);
        ImageView right = (ImageView)v.findViewById(R.id.actionbar_setting);
        right.setImageResource(resId);
        return right;
    }

    /**
     * 设置actionbar的自定义View
     */
    public static View setCustomTitle(final Activity act, boolean showBack, String t, View.OnClickListener clickSet) {
        act.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.xml.actionbar);

        ImageView back = (ImageView) act.findViewById(R.id.actionbar_back);
        ImageView set = (ImageView) act.findViewById(R.id.actionbar_setting);
        TextView title = (TextView) act.findViewById(R.id.actionbar_title);
        if (showBack) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.onBackPressed();
                }
            });
        } else {
            back.setVisibility(View.GONE);
        }
        if (clickSet == null) {
            set.setVisibility(View.GONE);
        } else {
            set.setOnClickListener(clickSet);
        }
        if (!TextUtils.isEmpty(t)) {
            title.setText(t);
        }
        return set;
    }

    public static void updateActionBarTitle(final Activity act, String title){
        final ActionBar ab = act.getActionBar();
        if(ab == null) return;
        View view = ab.getCustomView();
        TextView tv = (TextView)view.findViewById(R.id.actionbar_title);
        tv.setText(title);
    }


    /**
     * activity中fragment的切换
     */
    public static void replaceNewFragment(Activity act, int container_id, Fragment newF) {
        if (newF == null)
            return;
        Fragment cur = act.getFragmentManager().findFragmentById(container_id);
        Util.i("Activity=" + act + ", replaceNewFragment() cur=" + cur + ", new = " + newF);
        if (cur != newF) {
            FragmentTransaction ft = act.getFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
            ft.replace(container_id, newF);
            ft.addToBackStack(newF.toString());
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 获取sd卡信息
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableByte() {
        File data = Environment.getDataDirectory();
        String str = data.getPath();
        StatFs fs = new StatFs(str);
        long size2 = fs.getBlockSize();// from API 18, use getBlockSizeLong()
        long available = fs.getAvailableBlocks() * size2;
        return available;
    }

    /**
     * 获取当前可用内存大小
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        Log.d(TAG, "可用内存---->>>" + mi.availMem / (1024 * 1024));
        return mi.availMem / (1024 * 1024);
    }

    /**
     * 转到设置中的应用信息详情界面
     */
    public static void toAppDetail(Context c, String pkgName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package"/*SCHEME*/, pkgName, null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            c.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(c, "can not find app for " + pkgName, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prepare a custom preferences layout, moving padding to {@link ListView}
     * when outside scrollbars are requested. Usually used to display
     * {@link ListView} and {@link TabWidget} with correct padding.
     */
    public static void prepareCustomPreferencesList(
            ViewGroup parent, View child, View list, boolean ignoreSidePadding) {
        final boolean movePadding = list.getScrollBarStyle() == View.SCROLLBARS_OUTSIDE_OVERLAY;
        if (movePadding && parent instanceof PreferenceFrameLayout) {
            ((PreferenceFrameLayout.LayoutParams) child.getLayoutParams()).removeBorders = true;

            final Resources res = list.getResources();
            final int paddingSide = 5;//res.getDimensionPixelSize(R.dimen.settings_side_margin);
            final int paddingBottom = res.getDimensionPixelSize(
                    com.android.internal.R.dimen.preference_fragment_padding_bottom);

            final int effectivePaddingSide = ignoreSidePadding ? 0 : paddingSide;
            list.setPaddingRelative(effectivePaddingSide, 0, effectivePaddingSide, paddingBottom);
        }
    }

 
    public static String getNameForPackage(PackageManager pm, String pkgName) {
        String title = pkg2Name.get(pkgName);
        if (title == null) {
            try {
                title = pm.getApplicationInfo(pkgName, 0).loadLabel(pm).toString();
            } catch (NameNotFoundException e) {
                title = pkgName;
            }
            pkg2Name.put(pkgName, title);
        }
        return title;
    }

    public static Drawable getDrawableForPackage(PackageManager pm,
                                                 String pkgName) {
        Drawable icon = pkg2Drawable.get(pkgName);
        if (icon == null) {
            try {
                icon = pm.getApplicationInfo(pkgName, 0).loadIcon(pm);
            } catch (NameNotFoundException e) {
                icon = pm.getDefaultActivityIcon();
            }
            pkg2Drawable.put(pkgName, icon);
        }
        return icon;
    }

    public static void i(String msg) {
        android.util.Log.i(TAG, "" + msg);
    }

    public static void w(String msg) {
        android.util.Log.w(TAG, "" + msg);
    }

    public static void e(String msg) {
        android.util.Log.e(TAG, "" + msg);
    }

    boolean isRoot() {
        BufferedReader br;
        boolean flag = false;
        try {
            br = new BufferedReader(suTerminal("ls /data/"));   //目录哪都行，不一定要需要ROOT权限的
            if (br.readLine() != null)
                flag = true;  //根据是否有返回来判断是否有root权限
        } catch (Exception e1) {
        }
        return flag;
    }

    public InputStreamReader suTerminal(String command) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        // 执行到这，Superuser会跳出来，选择是否允许获取最高权限
        OutputStream outstream = process.getOutputStream();
        DataOutputStream DOPS = new DataOutputStream(outstream);
        InputStream instream = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(instream);
        String temp = command + "\n";
        // 加回车
        DOPS.writeBytes(temp);
        // 执行
        DOPS.flush();
        // 刷新，确保都发送到outputstream
        DOPS.writeBytes("exit\n");
        // 退出
        DOPS.flush();
        process.waitFor();
        return isr;
    }

    public static String getUsedTime(long time){

        Log.i(TAG,"time is " + time);
        long minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (long)((time % (1000 * 60)) / 1000.0 + 0.5);

        return minutes > 0 ? minutes + " 分 "+ seconds + " 秒" : seconds + " 秒";

    }


    private static HashMap<String, Integer> pkg2Uid = new HashMap<String, Integer>();
    public static int getUidForPkg(PackageManager pm, String pkg){
        int uid = 99999;
        Integer u = pkg2Uid.get(pkg);
        if(u != null)
            return u;
        try{
            uid = pm.getPackageUid(pkg, UserHandle.getCallingUserId());
            pkg2Uid.put(pkg, uid);
        }catch(PackageManager.NameNotFoundException e){}
        return uid;
    }
}
