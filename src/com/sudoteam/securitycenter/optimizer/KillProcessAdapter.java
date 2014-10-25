package com.sudoteam.securitycenter.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class KillProcessAdapter extends BaseAdapter implements IScan {
	private static final String TAG = "[KillProcessAdapter]";
	final ActivityManager mAM;
	final PackageManager mPM;
	final Context mContext;
	final Handler mHandler;
	List<KillItem> mList;
	List<String> mWhiteList = new ArrayList<String>();
	private static KillProcessAdapter ins;

	static KillProcessAdapter get(Context c, Handler h) {
		if (ins == null)
			ins = new KillProcessAdapter(c, h);
		return ins;
	}

	private KillProcessAdapter(Context c, Handler h) {
		mContext = c;
		mHandler = h;
		mPM = c.getPackageManager();
		mAM = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		// mList = getItems();
		mWhiteList.add(c.getPackageName());
		// mWhiteList.add("android");
		// mWhiteList.add("system");
		// mWhiteList.add("com.android.phone");
		// mWhiteList.add("com.android.systemui");
		// mWhiteList.add("com.android.nfc");
		// mWhiteList.add("com.android.keyguard");
		// mWhiteList.add("com.android.providers.telephony");
		// mWhiteList.add("com.android.smspush");
	}

	static class KillItem implements java.lang.Comparable<KillItem> {
		final RunningAppProcessInfo appInfo;
		boolean selected = true;

		KillItem(RunningAppProcessInfo p) {
			appInfo = p;
		}

		@Override
		public int compareTo(KillItem r) {
			int diff_im = r.appInfo.importance - appInfo.importance; // 按importance
																		// 降序
			if (diff_im != 0)
				return diff_im;
			else {
				return appInfo.processName.compareTo(r.appInfo.processName);// 按name升序
			}
		}
	}

	void refresh() {
		if (mList != null)
			mList.clear();
		mList = getItems();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public KillItem getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean isSelectAll() {
		if (mList != null)
			for (KillItem item : mList)
				if (!item.selected)
					return false;
		return true;
	}

	public void select(final boolean check) {
		if (mList == null)
			return;
		for (KillItem item : mList) {
			item.selected = check;
		}
		notifyDataSetChanged();
	}

	@Override
	public void destoryResult() {
		// TODO Auto-generated method stub

	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null)
			v = View.inflate(mContext, R.layout.kill_process_item, null);
		TextView title = (TextView) v.findViewById(R.id.kill_item_title);
		TextView size = (TextView) v.findViewById(R.id.kill_item_size);
		TextView impor = (TextView) v.findViewById(R.id.kill_item_importance);
		CheckBox cb = (CheckBox) v.findViewById(R.id.kill_item_cb);
		final KillItem one = (KillItem) getItem(position);
		if (one != null) {
			title.setText(one.appInfo.processName);
			// size.setText(one.appInfo.processName);
			size.setVisibility(View.GONE);
			impor.setText(" " + one.appInfo.importance);
			cb.setChecked(one.selected);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					one.selected = isChecked;
					notifyDataSetChanged();
				}
			});
		} else {
			String loading = "loading...";
			title.setText(loading);
			size.setText(loading);
			impor.setText(loading);
		}
		return v;
	}

	List<KillItem> getItems() {
		List<RunningAppProcessInfo> list = mAM.getRunningAppProcesses();
		ArrayList<KillItem> data = new ArrayList<KillItem>();
		if (list != null)
			for (RunningAppProcessInfo ra : list) {
				if (ra.importance <= RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
					continue;
				for (String pkgName : mWhiteList) {
					if (pkgName.equals(ra.processName))
						continue;
				}
				data.add(new KillItem(ra));
			}
		Collections.sort(data);
		return data;
	}

	/**
	 * kill(false) 杀死所选的app kill(true)杀死全部的app
	 * 
	 * @param killAll
	 *            是否kill所有的后台进程
	 * @return kill的进程数量
	 */

	@Override
	public int doCheck(Handler h) {
		if (mList != null)
			mList.clear();
		mList = getItems();
		Util.i("KillProcessAdapter=-- doCheck() size=" + mList.size());
		return getCurrentCount();
	}

	@Override
	public void clickItem(int pos) {
		Toast.makeText(mContext, "TODO.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int getCurrentCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public int optimizeSelect(boolean all) {
		int count = 0;
		final int size = mList.size();
		for (int i = 0; i < size; ++i) {
			KillItem item = mList.get(i);
			if (!item.selected && !all)
				continue;
			RunningAppProcessInfo rapi = item.appInfo;
			// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
			// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
			if (rapi.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
				String[] pkgList = rapi.pkgList;
				for (int j = 0; j < pkgList.length; ++j) {// pkgList 得到该进程下运行的包名
					Util.i(TAG + "It will be killed, package name : "
							+ pkgList[j] + ", pname" + rapi.processName
							+ ",, importance : " + rapi.importance);
					mAM.killBackgroundProcesses(pkgList[j]);
					count++;
				}
				mAM.forceStopPackage(rapi.processName);
			}
		}
		refresh();
		return count;
	}
}
