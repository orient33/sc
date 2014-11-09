package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * ops list.
 */
public class OpsListFragment extends ListFragment {
    public OpsListFragment() {
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
        AsyncTask<Void, Void, List<OneOps>> task = new AsyncTask<Void, Void, List<OneOps>>() {
            @Override
            public List<OneOps> doInBackground(Void... v) {
                int[] ops = {0};
                List<OneOps> list = new ArrayList<OneOps>();

                for (int i = 0; i < AppOpsManager._NUM_OP; ++i) {
                    ops[0] = i;
                    List<AppOpsManager.PackageOps> pops = aom.getPackagesForOps(ops);
                    String name = AppOpsManager.opToName(i);
                    int size = pops == null ? 0 : pops.size();
                    OneOps oo = new OneOps(i, name, size);
                    list.add(oo);
                }
                return list;
            }

            @Override
            public void onPostExecute(List<OneOps> data) {
                mAdapter.setData(data);
            }
        };
        task.execute();
    }

    static class OneOps {
        final String name;
        final int op;
        final int appsCount;

        OneOps(int op, String n, int count) {
            this.op = op;
            name = n;
            appsCount = count;
        }
    }

    static class AppAdapter extends BaseAdapter {
        List<OneOps> list;
        final LayoutInflater inflater;

        AppAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        /**
         * <must> call on UI thread
         */
        void setData(List<OneOps> data) {
            list = data;
            Util.i("ops list : setData() size == "+ data.size());
            notifyDataSetChanged();
        }

        @Override
        public OneOps getItem(int p) {
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
            final OneOps item = getItem(position);
            if (item == null) return v;
            final ViewHold vh;
            if (v == null) {
                v = inflater.inflate(R.layout.ops_opslist, null);
                vh = new ViewHold(v);
                v.setTag(vh);
            } else {
                vh = (ViewHold) v.getTag();
            }
            vh.op.setText("op:" + item.op);
            vh.name.setText(item.name);
            vh.size.setText(" app count :" + item.appsCount);

            return v;
        }
    }

    static class ViewHold {
        final TextView op, name, size;

        ViewHold(View v) {
            op = (TextView) v.findViewById(R.id.opslist_item_op);
            name = (TextView) v.findViewById(R.id.opslist_item_name);
            size = (TextView) v.findViewById(R.id.opslist_item_size);
        }
    }


}
