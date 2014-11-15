package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;

import java.util.ArrayList;
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
        final AppOpsManager aom = (AppOpsManager) act
                .getSystemService(Context.APP_OPS_SERVICE);
        AsyncTask<Void, Void, List<OneApp>> task = new AsyncTask<Void, Void, List<OneApp>>() {
            @Override
            public List<OneApp> doInBackground(Void... v) {
                return MacUtil.getAppsInfo(act, aom);
            }

            @Override
            public void onPostExecute(List<OneApp> data) {
                mAdapter.setData(data);
            }
        };
        task.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String pkg = mAdapter.getItem(position).pkgName;
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_PKG, pkg);
        getActivity().startActivity(intent);
    }

    static class OneApp {
        final Drawable icon;
        final String name, pkgName;
        final int uid;
        private int opsCount;
        private List<OpEntry> ops;

        OneApp(int u, String n, String p, Drawable i, List<OpEntry> list) {
            uid = u;
            name = n;
            pkgName = p;
            icon = i;
            opsCount = list == null ? 0 : list.size();
            ops = list;
        }

        List<OpEntry> getOps() {
            if (ops == null)
                ops = new ArrayList<OpEntry>();
            return ops;
        }

        boolean addOp(OpEntry oe) {
            if (hasOp(oe))
                return false;
            if (ops == null)
                ops = new ArrayList<OpEntry>();
            ops.add(oe);
            ++opsCount;
            return true;
        }

        boolean hasOp(OpEntry oe) {
            if (ops == null || ops.size() == 0)
                return false;
            for (OpEntry op : ops) {
                if (op.getOp() == oe.getOp()) {
                    MacUtil.i(" !  !  ! duplicate  op :" + oe.getOp());
                    return true;
                }
            }
            return false;
        }
    }

    static class AppAdapter extends BaseAdapter {
        List<OneApp> list;
        final LayoutInflater inflater;
        final String opsDescription;

        AppAdapter(Context c) {
            inflater = LayoutInflater.from(c);
            opsDescription = c.getResources().getString(R.string.app_has_ops);
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
            if (item == null)
                return v;
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
            vh.size.setText(item.opsCount + opsDescription);

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
