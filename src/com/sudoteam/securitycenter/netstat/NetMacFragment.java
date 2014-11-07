package com.sudoteam.securitycenter.netstat;

import android.Manifest;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络访问控制的UI
 */
public class NetMacFragment extends ListFragment {

    Activity mAct;
    LayoutInflater mLi;

    public NetMacFragment() {
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        mAct = getActivity();
        mLi = LayoutInflater.from(mAct);
        getLoaderManager().restartLoader(12, null, loaderCallback);
        Util.updateActionBarTitle(mAct, mAct.getString(R.string.data_usage_mac));
        setListAdapter(adapter);
    }

    private final LoaderManager.LoaderCallbacks<List<Item>> loaderCallback = new
            LoaderManager.LoaderCallbacks<List<Item>>() {
                @Override
                public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
                    return new ItemLoader(mAct);
                }

                @Override
                public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
                    setData(data);
                }

                @Override
                public void onLoaderReset(Loader<List<Item>> loader) {

                }
            };

    static class ItemLoader extends AsyncTaskLoader<List<Item>> {

        PackageManager mmPM;

        public ItemLoader(Context c) {
            super(c);
            mmPM = c.getPackageManager();
        }

        @Override
        public List<Item> loadInBackground() {
            ArrayList<Item> list = new ArrayList<Item>();
            PackageManager pm = mmPM;
            List<ApplicationInfo> apps = pm.getInstalledApplications(1);
            if (apps == null)
                apps = new ArrayList<ApplicationInfo>();
            for (ApplicationInfo ai : apps) {
                if (0 != (ai.flags & ApplicationInfo.FLAG_SYSTEM))
                    continue;
                try {
                    String permissions[] = pm.getPackageInfo(ai.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
                    if (permissions == null)
                        continue;
                    for (String permission : permissions) {
                        if (Manifest.permission.INTERNET.equals(permission)) {
                            Drawable icon = Util.getDrawableForPackage(pm, ai.packageName);
                            String label = Util.getNameForPackage(pm, ai.packageName);
                            int uid = pm.getPackageUid(ai.packageName, UserHandle.getCallingUserId());
                            Item ass = new Item(ai.packageName, label, uid, icon);
                            list.add(ass);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Util.e("NetMacFragment] " + e);
                    continue;
                }
            }
            return list;
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

    private List<Item> mList = new ArrayList<Item>();

    void setData(List<Item> list) {
        mList = list;
        adapter.notifyDataSetChanged();
    }

    private final BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Item getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final ViewHold vh;
            final Item item = getItem(position);
            if (v == null) {
                v = mLi.inflate(R.layout.data_usage_item, null);
                vh = new ViewHold(v);
                v.setTag(vh);
            } else {
                vh = (ViewHold) v.getTag();
            }

            vh.icon.setImageDrawable(item.icon);
            vh.name.setText(item.title);
            vh.used.setVisibility(View.GONE);
            vh.checkbox.setVisibility(View.VISIBLE);
            vh.checkbox.setChecked(true);//:TODO 根据当前策略 & response to click
            return v;
        }
    };

    class ViewHold {
        final ImageView icon;
        final TextView name, used;
        final Switch checkbox;

        ViewHold(View v) {
            icon = (ImageView) v.findViewById(android.R.id.icon);
            name = (TextView) v.findViewById(android.R.id.title);
            used = (TextView) v.findViewById(android.R.id.text1);
            checkbox = (Switch) v.findViewById(android.R.id.checkbox);
        }
    }

    static class Item {
        final String pkgName;
        final String title;
        final int uid;
        final Drawable icon;

        Item(String pn, String t, int u, Drawable i) {
            pkgName = pn;
            title = t;
            uid = u;
            icon = i;
        }
    }
}