package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * app list.
 */
public class AppListFragment extends ListFragment {
    public AppListFragment() {
        super();
    }

    private AppAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AppAdapter(getActivity());
        setListAdapter(mAdapter);
        final Activity act = getActivity();
        final AppOpsManager aom = (AppOpsManager) act.getSystemService(Context.APP_OPS_SERVICE);
        AsyncTask<Void, Void, List<OneApp>> task = new AsyncTask<Void, Void, List<OneApp>>() {
            @Override
            public List<OneApp> doInBackground(Void... v) {
                return getAppsInfo(act, aom);
            }

            @Override
            public void onPostExecute(List<OneApp> data) {
                mAdapter.setData(data);
            }
        };
        task.execute();
    }

    private List<OneApp> getAppsInfo(Context act, AppOpsManager aom) {
        final PackageManager pm = act.getPackageManager();
        List<OneApp> list = new ArrayList<OneApp>();
        List<ApplicationInfo> ais = pm.getInstalledApplications(0);
        if (ais == null) return list;


        final int ops[] = new int[AppOpsManager._NUM_OP];
        for (int ii = 0; ii < ops.length; ++ii)
            ops[ii] = ii;


        for (ApplicationInfo ai : ais) {
            if (0 != (ai.flags & ApplicationInfo.FLAG_SYSTEM))
                continue;


            Drawable icon = Util.getDrawableForPackage(pm, ai.packageName);
            String name = Util.getNameForPackage(pm, ai.packageName);
            List<AppOpsManager.PackageOps> ll = aom.getOpsForPackage(ai.uid, ai.packageName, ops);
            int size = ll == null ? 0 : ll.size();
            OneApp oa = new OneApp(ai.uid, name, ai.packageName, icon, size);
            list.add(oa);
        }

        return list;
    }


    static class OneApp {
        final Drawable icon;
        final String name, pkgName;
        final int uid;
        final int opsCount;

        OneApp(int u, String n, String p, Drawable i, int o) {
            uid = u;
            name = n;
            pkgName = p;
            icon = i;
            opsCount = o;
        }
    }

    static class AppAdapter extends BaseAdapter {
        List<OneApp> list;
        final LayoutInflater inflater;

        AppAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        /**
         * <must> call on UI thread
         */
        void setData(List<OneApp> data) {
            list = data;
            notifyDataSetChanged();
        }

        @Override
        public OneApp getItem(int p) {
            return list == null ? null : list.get(p);
        }

        @Override
        public long getItemId(int p) {
            return 0;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final OneApp item = getItem(position);
            if (item == null) return v;
            final ViewHold vh;
            if (v == null) {
                v = inflater.inflate(R.layout.ops_applist, null);
                vh = new ViewHold(v);
                v.setTag(vh);
            } else {
                vh = (ViewHold) v.getTag();
            }
            vh.icon.setImageDrawable(item.icon);
            vh.name.setText(item.name);
            vh.size.setText(item.opsCount + " op ");

            return v;
        }
    }

    static class ViewHold {
        final ImageView icon;
        final TextView name, size;

        ViewHold(View v) {
            icon = (ImageView) v.findViewById(R.id.applist_item_icon);
            name = (TextView) v.findViewById(R.id.applist_item_name);
            size = (TextView) v.findViewById(R.id.applist_item_size);
        }
    }

}
