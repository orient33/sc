package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.mac.AppListFragment.OneApp;

import java.util.ArrayList;
import java.util.List;

/**
 * detail information about one application's ops.
 */
public class AppDetail extends Fragment implements ListView.OnItemClickListener {
    static String KEY = "pkg-key";
    String mPkgName;
    Activity mAct;
    AppOpsManager mAom;
    OneApp mOneApp;
    ListView mListView;
    OpAdapter mAdapter;

    public AppDetail() {
    }

    @Override
    public void onAttach(Activity act) {
        super.onAttach(act);
        mAct = act;
        mAom = (AppOpsManager) act.getSystemService(Context.APP_OPS_SERVICE);
        mPkgName = getArguments().getString(KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c,
                             Bundle b) {
        View root = inflater.inflate(R.layout.ops_app_detail, null);
        ImageView icon = (ImageView) root.findViewById(R.id.ops_app_detail_icon);
        TextView name = (TextView) root.findViewById(R.id.ops_app_detail_name);
        mListView = (ListView) root.findViewById(R.id.ops_app_detail_list);
        mOneApp = MacUtil.getAppInfo(mAct, mAom, mPkgName);
        icon.setImageDrawable(mOneApp.icon);
        name.setText(mOneApp.name);
        mAdapter = new OpAdapter(mAct, mOneApp.getOps());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return root;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MyOpEntry moe = mAdapter.getItem(position);
        mTempOp = moe.op;
        new SetModeDialog().show(getFragmentManager(), "tag");
    }

    int mTempOp = 0;

    class SetModeDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder//.setTitle(R.string.pick_color)
                    .setItems(R.array.app_ops_permissions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Util.i("click which ? = " + which + ", op = " + mTempOp);
                            // 0 allow, 1 deny, 2 ask, 3 error ：TODO
                            mAom.setMode(mTempOp, mOneApp.uid, mOneApp.pkgName, which);
                            mAdapter.modifyOneOp(mTempOp,which);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
            return builder.create();
        }
    }

    static class OpAdapter extends BaseAdapter {
        final List<MyOpEntry> mmOps;
        final LayoutInflater mmLi;

        OpAdapter(Context c, List<AppOpsManager.OpEntry> data) {
            mmLi = LayoutInflater.from(c);
            MacUtil.getLabelForOp(c, 0);// to init string..
            MacUtil.getLabelForMode(c, 0);
            mmOps = MyOpEntry.getFromOpEntry(data);
        }

        void modifyOneOp(int op, int mode){
            for(MyOpEntry moe : mmOps){
                if(moe.op == op) {
                    moe.mode = mode;
                    return;
                }
            }
        }

        @Override
        public int getCount() {
            return mmOps == null ? 0 : mmOps.size();
        }

        @Override
        public MyOpEntry getItem(int i) {
            return mmOps == null ? null : mmOps.get(i);
        }

        @Override
        public long getItemId(int p) {
            return 0;
        }

        @Override
        public View getView(int p, View v, ViewGroup par) {
            MyOpEntry oe = getItem(p);
            if (v == null)
                v = mmLi.inflate(R.layout.ops_app_detail_item, null);
            TextView name = (TextView) v.findViewById(R.id.ops_app_detail_op_name);
            TextView mode = (TextView) v.findViewById(R.id.ops_app_detail_op_mode);
            name.setText(MacUtil.getLabelForOp(null, oe.op));
            mode.setText(MacUtil.getLabelForMode(null, oe.mode));
            return v;
        }
    }

    static class MyOpEntry {
        int op, mode;

        MyOpEntry(int o, int m) {
            op = o;
            mode = m;
        }
        static List<MyOpEntry> getFromOpEntry(List<AppOpsManager.OpEntry> data){
            List<MyOpEntry> list = new ArrayList<MyOpEntry>();
            for(AppOpsManager.OpEntry oe : data){
                MyOpEntry moe = new MyOpEntry(oe.getOp(),oe.getMode());
                list.add(moe);
            }
            return list;
        }
    }
}
