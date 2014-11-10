package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * detail information about one application's ops.
 */
public class OpsDetail extends Fragment implements AdapterView.OnItemClickListener {
    static String KEY = "ops-key";
    int mOp;

    public OpsDetail() {
    }

    Activity mAct;
    AppOpsManager mAom;
    OpsListFragment.OneOp mOneOp;
    ListView mListView;
    OpAdapter mAdapter;
    PackageManager mPm;

    @Override
    public void onAttach(Activity act) {
        super.onAttach(act);
        mAct = act;
        mAom = (AppOpsManager) act.getSystemService(Context.APP_OPS_SERVICE);
        mOp = getArguments().getInt(KEY);
        mPm = act.getPackageManager();
//        mOneOp = getArguments().get
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c,
                             Bundle b) {
        View root = inflater.inflate(R.layout.ops_op_detail, null);
        TextView name = (TextView) root.findViewById(R.id.ops_op_detail_name);
        mListView = (ListView) root.findViewById(R.id.ops_op_detail_list);
        mOneOp = MacUtil.getOpInfo(mAct, mAom, mOp);
        name.setText(mOneOp.name);
        mAdapter = new OpAdapter(mAct, mOneOp);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return root;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTemp = mAdapter.getItem(position).pkg;
        new SetModeDialog().show(getFragmentManager(), "tag");
    }

    String mTemp = "";

    class SetModeDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder//.setTitle(R.string.pick_color)
                    .setItems(R.array.app_ops_permissions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 0 allow, 1 deny, 2 ask, 3 error ：TODO
                            int uid = 0;
                            try {
                                uid = mPm.getPackageUid(mTemp, UserHandle.getCallingUserId());
                            } catch (PackageManager.NameNotFoundException e) {
                                return;
                            }
                            mAom.setMode(mOp, uid, mTemp, which);
                            mAdapter.modifyOneOp(mTemp, which);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
            return builder.create();
        }
    }

    static class OpAdapter extends BaseAdapter {
        final List<MyAppEntry> mmPkgs;
        final LayoutInflater mmLi;
        final PackageManager mmPm;

        OpAdapter(Context c, OpsListFragment.OneOp data) {
            mmLi = LayoutInflater.from(c);
            mmPm = c.getPackageManager();
            MacUtil.getLabelForOp(c, 0);// to init string..
            MacUtil.getLabelForMode(c, 0);
            mmPkgs = MyAppEntry.getFromPkgs(data);
        }

        void modifyOneOp(String op, int mode) {
            for (MyAppEntry mae : mmPkgs) {
                if (mae.pkg.equals(op)) {
                    mae.mode = mode;
                    return;
                }
            }
        }

        @Override
        public int getCount() {
            return mmPkgs == null ? 0 : mmPkgs.size();
        }

        @Override
        public MyAppEntry getItem(int i) {
            return mmPkgs == null ? null : mmPkgs.get(i);
        }

        @Override
        public long getItemId(int p) {
            return 0;
        }

        @Override
        public View getView(int p, View v, ViewGroup par) {
            MyAppEntry oe = getItem(p);
            if (v == null)
                v = mmLi.inflate(R.layout.ops_op_detail_item, null);
            TextView name = (TextView) v.findViewById(R.id.ops_op_detail_op_name);
            TextView mode = (TextView) v.findViewById(R.id.ops_op_detail_op_mode);
            name.setText(Util.getNameForPackage(mmPm, oe.pkg));
            mode.setText(MacUtil.getLabelForMode(null, oe.mode));
            return v;
        }
    }

    static class MyAppEntry {
        String pkg;
        int mode;

        MyAppEntry(String p, int m) {
            pkg = p;
            mode = m;
        }

        static List<MyAppEntry> getFromPkgs(OpsListFragment.OneOp oo) {
            List<MyAppEntry> list = new ArrayList<MyAppEntry>();
            for (int i = 0; i < oo.modes.size(); ++i) {
                MyAppEntry mae = new MyAppEntry(oo.pkgs.get(i), oo.modes.get(i));
                list.add(mae);
            }
            return list;
        }
    }
}
