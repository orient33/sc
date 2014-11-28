
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
        int uid = mAdapter.getItem(position).uid;
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_PKG, pkg);
        intent.putExtra(DetailActivity.KEY_UID, uid);
        getActivity().startActivity(intent);
    }

    static class OpMode implements Comparable<OpMode> {
        private int op, mode;

        OpMode(int o, int m) {
            op = o;
            mode = m;
        }

        public void changeMode(int m) {
            if (mode != m)
                mode = m;
        }

        public int getOp() {
            return op;
        }

        public int getMode() {
            return mode;
        }

        @Override
        public int compareTo(OpMode another) {
            boolean strict = AppOpsManager.isStrictOp(op), strict2 = AppOpsManager
                    .isStrictOp(another.op);
            if (strict != strict2) {
                if (strict)
                    return -1;
                else
                    return 1;
            }
            return op - another.op;
        }
    }

    static class OneApp {
        final Drawable icon;
        final String name, pkgName;
        final int uid;
        private int opsCount;
        private final List<OpMode> ops;
        private final List<OpMode> opSwitches = new ArrayList<OpMode>();

        OneApp(int u, String n, String p, Drawable i, List<OpEntry> list) {
            uid = u;
            name = n;
            pkgName = p;
            icon = i;
            opsCount = list == null ? 0 : list.size();
            ops = new ArrayList<OpMode>();
            if (list != null)
                for (OpEntry oe : list)
                    ops.add(new OpMode(oe.getOp(), oe.getMode()));

        }

        List<OpMode> getOpsSwitches() {
            return opSwitches;
        }

        List<OpMode> getOps() {
            return ops;
        }

        boolean addOp(int op, int mode) {
            if (hasOp(op))
                return false;
            ops.add(new OpMode(op, mode));
            ++opsCount;

            return true;
        }

        void buildOpSwitch() {
            if (opSwitches.size() > 0)
                return;
            for (OpMode oe : ops) {
                int op_switch = AppOpsManager.opToSwitch(oe.getOp());
                boolean has = false;
                for (OpMode oe2 : opSwitches) {
                    if (op_switch == AppOpsManager.opToSwitch(oe2.getOp())) {
                        has = true;
                        break;
                    }
                }
                if (!has)
                    opSwitches.add(new OpMode(op_switch, oe.getMode()));
            }
            MacUtil.i("buildOpSwitch  over. ops.size=" + ops.size() + ",, switches.size = "
                    + opSwitches.size());
        }

        private boolean hasOp(int opid) {
            if (ops == null || ops.size() == 0)
                return false;
            for (OpMode op : ops) {
                if (op.getOp() == opid) {
                    // MacUtil.i(" !  !  ! duplicate  op :" + opid);
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
