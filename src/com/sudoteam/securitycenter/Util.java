package com.sudoteam.securitycenter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceFrameLayout;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.Toast;

public class Util {
	public static String TAG = "godin";
	
	public static void replaceNewFragment(Activity act,int container_id, Fragment newF) {
		if (newF == null)
			return;
		Fragment cur = act.getFragmentManager().findFragmentById(container_id);
		Util.i("Activity="+act+", replaceNewFragment() cur=" + cur + ", new = " + newF);
		if (cur != newF) {
			FragmentTransaction ft = act.getFragmentManager().beginTransaction();
			ft.replace(container_id, newF);
			ft.addToBackStack(newF.toString());
			ft.commitAllowingStateLoss();
		}
	}
	
/**获取sd卡信息*/
	@SuppressWarnings("deprecation")
	public static long getAvailableByte() {
		File data = Environment.getDataDirectory();
		String str = data.getPath();
		StatFs fs = new StatFs(str);
		long size2 = fs.getBlockSize();// from API 18, use getBlockSizeLong()
		long available = fs.getAvailableBlocks() * size2;
		return available;
	}

	
	/**获取当前可用内存大小*/
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
	/** 转到设置中的应用信息详情界面 */
	public static void toAppDetail(Context c, String pkgName){
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  
		Uri uri = Uri.fromParts("package"/*SCHEME*/, pkgName, null);  
		intent.setData(uri);  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try{
			c.startActivity(intent);
		}catch(ActivityNotFoundException e){
			Toast.makeText(c, "can not find app for "+ pkgName, Toast.LENGTH_SHORT).show();
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
    
	/** 查询是否root了 */
	public static boolean isRooted(Context c) {
		String rooted = c.getString(R.string.rooted), un_root = c
				.getString(R.string.root_unavalable);
		try {
			String result = execShell("type su"); // eg : su is tracked alias for /system/xbin/su
			String su_path = result.substring(result.lastIndexOf(" ")); // eg : /system/xbin/su
			result = execShell("md5  " + su_path);
			String md5 = result.substring(0, result.indexOf(" "));
			boolean noRoot = TextUtils.isEmpty(md5);
			Toast.makeText(c, (noRoot ? un_root : rooted) + md5, Toast.LENGTH_LONG).show();
			return !noRoot;
		} catch (Exception e) {
			e("[Util] "+e);
		}
		return false;
	}

	public static String execShell(String cmd){
		String[] cmdStrings = new String[] {"sh", "-c", cmd};
		Runtime run = Runtime.getRuntime();
		StringBuilder sb = new StringBuilder("");
		try {
			Process proc = run.exec(cmdStrings);
			InputStream in = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line="";
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			if (proc.waitFor() != 0) {

			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return sb.toString();
	}
	

	 boolean isRoot(){
		 BufferedReader br;  
        boolean flag=false;  
        try {  
            br = new BufferedReader(suTerminal("ls /data/"));   //目录哪都行，不一定要需要ROOT权限的  
            if(br.readLine()!=null)
            	flag=true;  //根据是否有返回来判断是否有root权限  
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

	public static void i(String msg){
		android.util.Log.i(TAG, ""+msg);
	}
	
	public static void w(String msg){
		android.util.Log.w(TAG, ""+msg);
	}
	public static void e(String msg){
		android.util.Log.e(TAG, ""+msg);
	}
}
