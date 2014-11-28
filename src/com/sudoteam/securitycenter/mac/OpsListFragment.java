package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

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
        AsyncTask<Void, Void, List<OneOp>> task = new AsyncTask<Void, Void, List<OneOp>>() {
            @Override
            public List<OneOp> doInBackground(Void... v) {
                return MacUtil.getOpsList(act, aom);
            }

            @Override
            public void onPostExecute(List<OneOp> data) {
                mAdapter.setData(data);
            }
        };
        task.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int op = mAdapter.getItem(position).op;
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_OP, op);
        getActivity().startActivity(intent);
    }

    static class OneOp {
        final String name;
        final int op;
        final int appsCount;
        final List<String> pkgs;

        OneOp(int op, String n, int count, List<String> data) {
            this.op = op;
            name = n;
            appsCount = count;
            pkgs = data;
        }
    }

    static class AppAdapter extends BaseAdapter {
        List<OneOp> list;
        final LayoutInflater inflater;

        private String ops_use_count;

        AppAdapter(Context c) {
            inflater = LayoutInflater.from(c);
            MacUtil.getLabelForOp(c, 0); // to init string...
            ops_use_count = c.getString(R.string.ops_use_count);
        }

        /**
         * <must> call on UI thread
         */
        void setData(List<OneOp> data) {
            list = data;
            Util.i("ops list : setData() size == " + data.size());
            notifyDataSetChanged();
        }

        @Override
        public OneOp getItem(int p) {
            return list == null ? null : list.get(p);
        }

        @Override
        public long getItemId(int p) {
            return p;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final OneOp item = getItem(position);
            if (item == null) return v;
            final ViewHold vh;
            if (v == null) {
                v = inflater.inflate(R.layout.ops_opslist, null);
                vh = new ViewHold(v);
                v.setTag(vh);
            } else {
                vh = (ViewHold) v.getTag();
            }
            vh.op.setText(MacUtil.getLabelForOp(null, item.op));
            vh.size.setText(item.appsCount + ops_use_count);

            return v;
        }
    }

    static class ViewHold {
        final TextView op, size;

        ViewHold(View v) {
            op = (TextView) v.findViewById(R.id.opslist_item_name);
//            summary = (TextView) v.findViewById(R.id.opslist_item_summary);
            size = (TextView) v.findViewById(R.id.opslist_item_size);
        }
    }


}
