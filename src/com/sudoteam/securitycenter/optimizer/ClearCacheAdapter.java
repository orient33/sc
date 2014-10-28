package com.sudoteam.securitycenter.optimizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class ClearCacheAdapter extends BaseAdapter implements IScan {

	private static final String TAG = "[ClearCacheAdapter]";

	/** 小于此值的忽略, 即 认为无缓存 */
	private static final int MIN_CACHE = 12288;
	private static ClearCacheAdapter ins;
	final Context mContext;
	final PackageManager mPM;
	List<AppCacheInfo> mList;
	private ISelectCallback callback;

	static ClearCacheAdapter get(Context c, ISelectCallback cb) {
		if (ins == null)
			ins = new ClearCacheAdapter(c, cb);
		return ins;
	}
	
	public void setCallback(ISelectCallback cb){
		callback = cb;
	}

	private ClearCacheAdapter(Context c, ISelectCallback cb) {
		mContext = c.getApplicationContext();
		mPM = c.getPackageManager();
		callback = cb;
	}

	public static class AppCacheInfo {
		static Integer findApp = 0;
		final File apkFile;
		final long id;
		long cacheSize;
		String label;
		boolean mounted, selected=true;
		ApplicationInfo info;
		Drawable icon;

		AppCacheInfo(Context context, ApplicationInfo info, long id) {
			apkFile = new File(info.sourceDir);
			this.id = id;
			this.info = info;
			ensureLabel(context);
			ensureIconLocked(context);
		}

		private void ensureLabel(Context context) {
			if (this.label == null || !this.mounted) {
				if (!this.apkFile.exists()) {
					this.mounted = false;
					this.label = info.packageName;
				} else {
					this.mounted = true;
					CharSequence label = info.loadLabel(context
							.getPackageManager());
					this.label = label != null ? label.toString()
							: info.packageName;
				}
			}
		}

		boolean ensureIconLocked(Context context) {
			PackageManager pm = context.getPackageManager();
			if (this.icon == null) {
				if (this.apkFile.exists()) {
					this.icon = this.info.loadIcon(pm);
					return true;
				} else {
					this.mounted = false;
					this.icon = context
							.getResources()
							.getDrawable(
									com.android.internal.R.drawable.sym_app_on_sd_unavailable_icon);
				}
			} else if (!this.mounted) {
				// If the app wasn't mounted but is now mounted, reload its
				// icon.
				if (this.apkFile.exists()) {
					this.mounted = true;
					this.icon = this.info.loadIcon(pm);
					return true;
				}
			}
			return false;
		}

		IPackageStatsObserver.Stub pso = new IPackageStatsObserver.Stub() {
			@Override
			public void onGetStatsCompleted(PackageStats ps, boolean succeeded)
					throws RemoteException {
				cacheSize = ps.cacheSize;
				synchronized (findApp) {
					--findApp;
				}
			}
		};
	}
	void refresh() {
		if (mList != null)
			mList.clear();
		doCheck(null,0);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public AppCacheInfo getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean isSelectAll() {
		if (mList != null)
			for (AppCacheInfo item : mList)
				if (!item.selected)
					return false;
		return true;
	}

	public void select(final boolean check) {
		if (mList == null)
			return;
		for (AppCacheInfo item : mList) {
			item.selected = check;
		}
		notifyDataSetChanged();
	}

	@Override
	public void destoryResult() {
		if (mList != null)
			mList.clear();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHold vh;
		final AppCacheInfo aci = (AppCacheInfo) getItem(position);
		if (v == null) {
			v = View.inflate(mContext, R.layout.clear_cache_item, null);
			vh = new ViewHold(v);
			v.setTag(vh);
		} else {
			vh = (ViewHold)v.getTag();
		}
		
		if (aci != null) {
			vh.title.setText(aci.label);
			vh.size.setText(Formatter.formatFileSize(mContext, aci.cacheSize));
			vh.cb.setChecked(aci.selected);
			vh.iv.setImageDrawable(aci.icon);
			vh.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton bv,
						boolean isChecked) {
					aci.selected = isChecked;
					if(callback != null)
						callback.selectChanged();
					notifyDataSetChanged();
				}
			});
		} else {
			vh.title.setText("loading...");
		}
		return v;
	}
	class ViewHold{
		TextView title, size;
		ImageView iv;
		CheckBox cb;
		ViewHold(View v){
			title = (TextView) v.findViewById(R.id.clear_item_title);
			size = (TextView) v.findViewById(R.id.clear_item_size);
			cb = (CheckBox) v.findViewById(R.id.clear_item_cb);
			iv = (ImageView)v.findViewById(R.id.clear_item_icon);
		}
	}

	/** 必须在 非UI线程 执行 
	 * @param h 进度观察
	 * @return 垃圾/缓存总大小 */
	@Override
	public int doCheck(Handler h, int what) {
		final long start = System.currentTimeMillis();
		PackageManager pm = mPM;
		final ArrayList<AppCacheInfo> data = new ArrayList<AppCacheInfo>();
		List<ApplicationInfo> list = pm.getInstalledApplications(0);
		synchronized (AppCacheInfo.findApp) {
			AppCacheInfo.findApp = 0;
			for (ApplicationInfo ai : list) {
				AppCacheInfo aci = new AppCacheInfo(mContext, ai, 0l);
				pm.getPackageSizeInfo(ai.packageName, aci.pso);
				if(h != null){
					h.obtainMessage(what, aci.label).sendToTarget();
					SystemClock.sleep(10);
				}
				data.add(aci);
				++AppCacheInfo.findApp;
			}
		}
		if(h != null)
			h.obtainMessage(what, "").sendToTarget();
		Util.i("load app info , count = " + data.size());
		int maxLoop = 10;
		while (AppCacheInfo.findApp > 0 && maxLoop-- > 0)
			// 确保回调执行
			SystemClock.sleep(100);
		for (int i = data.size() - 1; i >= 0; --i) {
			if (data.get(i).cacheSize <= MIN_CACHE)
				data.remove(i);
		}
		if (AppCacheInfo.findApp > 0)
			Util.e("call back is less. findApp == " + AppCacheInfo.findApp);
		Util.i(" after check = " + data.size() + ". use time:"
				+ (System.currentTimeMillis() - start) + " ms");
		mList = data;
		return getCurrentCount();
	}

	@Override
	public int getCurrentCount() {
		if (mList != null && mList.size() > 0) {
			int count = 0;
			for (AppCacheInfo aci : mList)
				count += aci.cacheSize;
			return count;
		}
		return 0;
	}

	@Override
	public int optimizeSelect(boolean all) {
		if (mList == null)
			return 0;
		long before = Util.getAvailableByte();
		long count = 0l;
		for (AppCacheInfo aci : mList) {
			if (aci.selected || all) {
				mPM.deleteApplicationCacheFiles(aci.info.packageName, null);
				count += aci.cacheSize;
			}
		}
		long after = Util.getAvailableByte();
		Util.i("实际值 : " + (after - before) + ",,, 应该是 ： " + count + ",,, 相差: "
				+ (after - before - count));
		refresh();
		return (int)count;
	}
}
