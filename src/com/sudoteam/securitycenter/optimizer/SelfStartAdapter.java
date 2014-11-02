package com.sudoteam.securitycenter.optimizer;

import android.app.AppOpsManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

public class SelfStartAdapter extends BaseAdapter {
    private static final String TAG = "[SelfStartAdapter]";
    final Context mContext;

    ;
    final AppOpsManager mAOM;
    final PackageManager mPm;
    final String mAllowed, mForbidden;
    private final View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            final AppSelfStart ass = getItem(index);
            int uid = ass.uid;
            int mode = !ass.allow ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_IGNORED;
            try {
                mAOM.setMode(AppOpsManager.OP_BOOT_COMPLETED, uid, ass.pkgName, mode);
                ass.allow = !ass.allow;
                notifyDataSetChanged();
                Util.i("setmode() name = " + ass.label + ", mode " + mode);
            } catch (Exception e) {
                Toast.makeText(mContext, "" + e, Toast.LENGTH_SHORT).show();
            }
        }
    };
    List<AppSelfStart> mList;
    SelfStartAdapter(Context c) {
        mContext = c;
        mPm = c.getPackageManager();
        mAOM = (AppOpsManager) c.getSystemService(Context.APP_OPS_SERVICE);
        mAllowed = c.getString(R.string.allowed);
        mForbidden = c.getString(R.string.forbidden);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public AppSelfStart getItem(int position) {
        if (mList == null)
            return null;
        if (mList.size() <= position)
            return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList == null ? 0 : position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final AppSelfStart ass = getItem(position);
        ViewHold vh;
        if (v == null) {
            v = View.inflate(mContext, R.layout.self_start_item, null);
            vh = new ViewHold(v, position);
            v.setTag(vh);
        } else {
            vh = (ViewHold) v.getTag();
        }
        if (ass == null) {
            vh.label.setText("loading...");
        } else {
            vh.icon.setImageDrawable(ass.icon);
            vh.label.setText(ass.label);
            vh.allow.setText(ass.allow ? mAllowed : mForbidden);
            vh.allow.setTag((Integer) position);
        }
        return v;
    }

    void setData(List<AppSelfStart> data) {
        mList = data;
        notifyDataSetChanged();
    }

    static class SelfStartLoader extends AsyncTaskLoader<List<AppSelfStart>> {
        final PackageManager mPm;
        final AppOpsManager mAppOpsManager;

        public SelfStartLoader(Context context) {
            super(context);
            mPm = context.getPackageManager();
            mAppOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        }

        @Override
        public List<AppSelfStart> loadInBackground() {
            List<ApplicationInfo> apps = mPm.getInstalledApplications(1);
            if (apps == null)
                apps = new ArrayList<ApplicationInfo>();

            Util.i(TAG + " loadInBackground() ");
            final List<AppSelfStart> data = new ArrayList<AppSelfStart>();
            for (ApplicationInfo ai : apps) {
                if (0 != (ai.flags & ApplicationInfo.FLAG_SYSTEM))
                    continue;
                try {
                    String permissions[] = mPm.getPackageInfo(ai.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
                    if (permissions == null)
                        continue;
                    for (String permission : permissions) {
                        if (android.Manifest.permission.RECEIVE_BOOT_COMPLETED
                                .equals(permission)) {
                            Drawable icon = Util.getDrawableForPackage(mPm, ai.packageName);
                            String label = Util.getNameForPackage(mPm, ai.packageName);
                            int uid = mPm.getPackageUid(ai.packageName, UserHandle.getCallingUserId());
                            int mode = mAppOpsManager.checkOp(AppOpsManager.OP_BOOT_COMPLETED, uid, ai.packageName);
                            boolean allow = mode == AppOpsManager.MODE_ALLOWED;
                            AppSelfStart ass = new AppSelfStart(ai.packageName, uid, label, icon, allow);
                            data.add(ass);
                        }
                    }
                } catch (NameNotFoundException e) {
                    Util.e("SelfStartAdapter] " + e);
                    continue;
                }
            }
            Util.i(TAG + " loadInBackground()  finish()");
            return data;
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    static class AppSelfStart {
        final Drawable icon;
        String label, summary, pkgName;
        int uid;
        boolean allow;

        AppSelfStart(String p, int u, String l, Drawable i, boolean a) {
            pkgName = p;
            uid = u;
            label = l;
            icon = i;
            allow = a;
        }
    }

    class ViewHold {
        ImageView icon;
        TextView label, summary, allow;

        ViewHold(View r, int index) {
            icon = (ImageView) r.findViewById(R.id.self_item_icon);
            label = (TextView) r.findViewById(R.id.slef_item_title);
            allow = (TextView) r.findViewById(R.id.self_item_allow);
            allow.setOnClickListener(lis);
        }
    }

}
