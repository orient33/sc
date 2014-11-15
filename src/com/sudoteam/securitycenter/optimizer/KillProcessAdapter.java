package com.sudoteam.securitycenter.optimizer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KillProcessAdapter extends BaseAdapter implements IScan {
    private static final String TAG = "[KillProcessAdapter]";
    private static KillProcessAdapter ins;
    final ActivityManager mAM;
    final PackageManager mPM;
    final Context mContext;
    Handler mHandler;
    private final ISelectCallback callback;
    List<KillItem> mList;
    private final KillProcessWhiteList mWhiteList;
    private final String mInWhite;

    private KillProcessAdapter(Context c, ISelectCallback cb) {
        mContext = c;
        callback = cb;
        mPM = c.getPackageManager();
        mAM = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        mInWhite = c.getString(R.string.already_in_white);
        // mList = getItems();
        mWhiteList = KillProcessWhiteList.get(c);
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

    static KillProcessAdapter get(Context c, ISelectCallback cb) {
        if (ins == null)
            ins = new KillProcessAdapter(c, cb);
        return ins;
    }

    KillProcessAdapter setHandler(Handler h) {
        mHandler = h;
        return this;
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
        if (mList == null)
            return null;
        if (mList.size() <= position)
            return null;
        return mList.get(position);
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
        if (mList != null)
            mList.clear();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHold vh;
        final KillItem one = getItem(position);

        if (v == null) {
            v = View.inflate(mContext, R.layout.kill_process_item, null);
            vh = new ViewHold(v);
            v.setTag(vh);
        } else {
            vh = (ViewHold) v.getTag();
        }
        if (one != null) {
            vh.title.setText(one.title + "");
            vh.size.setText(one.memUse);
            vh.icon.setImageDrawable(one.icon);
            if (one.inWhite) {
                vh.white.setText(mInWhite);
                vh.cb.setVisibility(View.INVISIBLE);
            } else {
                vh.white.setText("");
                vh.cb.setChecked(one.selected);
                vh.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        one.selected = isChecked;
                        if (callback != null)
                            callback.selectChanged();
                        notifyDataSetChanged();
                    }
                });
                vh.cb.setVisibility(View.VISIBLE);
            }
        } else {
            String loading = "loading...";
            vh.title.setText(loading);
            vh.size.setText(loading);
        }
        return v;
    }

    private List<KillItem> getItems() {
        List<RunningAppProcessInfo> list = mAM.getRunningAppProcesses();
        ArrayList<KillItem> data = new ArrayList<KillItem>();
        if (list != null)
            for (RunningAppProcessInfo ra : list) {
                if (ra.importance <= RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                    continue;
//                if (mWhiteList.isWhite(ra.processName))
//                    continue;
                data.add(new KillItem(ra));
            }
        Collections.sort(data);
        return data;
    }

    /**
     *
     * @return size of running app which can be killed
     */

    @Override
    public int doCheck(Handler h, int what) {
        if (mList != null)
            mList.clear();
        String des = mContext.getResources().getString(R.string.kill_app_des);
        h.obtainMessage(OptimizerFragment.MSG_UPDATE_PROGRESS, des).sendToTarget();
        mList = getItems();
        Util.i("KillProcessAdapter=-- doCheck() size=" + mList.size());
        return getCurrentCount();
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
            if (item.inWhite) continue;
            RunningAppProcessInfo rapi = item.appInfo;
            if (rapi.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                String[] pkgList = rapi.pkgList;
                for (int j = 0; j < pkgList.length; ++j) {// pkgList 得到该进程下运行的包名
                    Util.i(TAG + "It will be killed, package name : " + ", pname" + item.title
                            + ",, importance : " + rapi.importance);
                    mAM.killBackgroundProcesses(pkgList[j]);
                    count++;
                }
                mAM.forceStopPackage(rapi.processName);
            }
        }
        if (!all)
            refresh();
        return count;
    }

    class KillItem implements java.lang.Comparable<KillItem> {
        final RunningAppProcessInfo appInfo;
        boolean selected = true;
        boolean inWhite;
        Drawable icon;
        String title;
        String memUse;

        KillItem(RunningAppProcessInfo p) {
            appInfo = p;

            title = Util.getNameForPackage(mPM, appInfo.processName);
            icon = Util.getDrawableForPackage(mPM, appInfo.processName);
            inWhite = mWhiteList.isWhite(appInfo.processName);
            Debug.MemoryInfo[] mi = mAM.getProcessMemoryInfo(new int[]{appInfo.pid});
            memUse = Formatter.formatFileSize(mContext, mi[0].getTotalPss() * 1024);
        }

        @Override
        public int compareTo(KillItem r) {
            if (r.inWhite != inWhite) {
                if (r.inWhite) return -1;
                else return 1;
            }
            int diff_im = r.appInfo.importance - appInfo.importance; // 按importance
            // desc
            if (diff_im != 0)
                return diff_im;
            else {
                return appInfo.processName.compareTo(r.appInfo.processName);// 按name升序
            }
        }
    }

    class ViewHold {
        TextView title, size, white;
        CheckBox cb;
        ImageView icon;

        ViewHold(View v) {
            title = (TextView) v.findViewById(R.id.kill_item_title);
            size = (TextView) v.findViewById(R.id.kill_item_size);
            white = (TextView) v.findViewById(R.id.kill_item_white);
            cb = (CheckBox) v.findViewById(R.id.kill_item_cb);
            icon = (ImageView) v.findViewById(R.id.kill_item_icon);
        }
    }
}
