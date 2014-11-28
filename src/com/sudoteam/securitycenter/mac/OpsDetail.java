
package com.sudoteam.securitycenter.mac;

import static com.sudoteam.securitycenter.mac.MacUtil.modeToPosition;
import static com.sudoteam.securitycenter.mac.MacUtil.positionToMode;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * detail information about one application's ops.
 */
public class OpsDetail extends Fragment {
    int mOp;
    boolean mIsStrict;

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
        mOp = getArguments().getInt(DetailActivity.KEY_OP);
        mPm = act.getPackageManager();
        mIsStrict = AppOpsManager.isStrictOp(mOp);
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
        return root;
    }

    class OpAdapter extends BaseAdapter {
        final List<MyAppEntry> mmPkgs;
        final LayoutInflater mmLi;
        final PackageManager mmPm;

        OpAdapter(Context c, OpsListFragment.OneOp data) {
            mmLi = LayoutInflater.from(c);
            mmPm = c.getPackageManager();
            MacUtil.getLabelForOp(c, 0);// to init string..
            MacUtil.getLabelForMode(c, 0);
            mmPkgs = MyAppEntry.getFromPkgs(data, mAom, mOp, mmPm);
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
            final MyAppEntry oe = getItem(p);
            // if (v == null)
            v = mmLi.inflate(R.layout.ops_op_detail_item, null);
            TextView name = (TextView) v.findViewById(R.id.ops_op_detail_op_name);
            name.setText(Util.getNameForPackage(mmPm, oe.pkg));
            Spinner sp = (Spinner) v.findViewById(R.id.ops_op_detail_sp);
            Switch sw = (Switch) v.findViewById(R.id.ops_op_detail_sw);
            final int mode = oe.mode;
            final int uid = Util.getUidForPkg(mmPm, oe.pkg);

            sp.setSelection(modeToPosition(mode));
            sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                boolean firstMode = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                        long id) {
                    if (firstMode) {
                        firstMode = false;
                        return;
                    }
                    MacUtil.setMyMode(mOp, uid, oe.pkg, positionToMode(position), mAom);
                    modifyOneOp(oe.pkg, positionToMode(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            sw.setChecked(mode == AppOpsManager.MODE_ALLOWED);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int mmode = isChecked ? AppOpsManager.MODE_ALLOWED
                            : AppOpsManager.MODE_IGNORED;
                    MacUtil.setMyMode(mOp, uid, oe.pkg, mmode, mAom);
                    modifyOneOp(oe.pkg, mmode);
                }
            });
            if (mIsStrict) {
                sp.setVisibility(View.VISIBLE);
            } else {
                sw.setVisibility(View.VISIBLE);
            }
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

        static List<MyAppEntry> getFromPkgs(OpsListFragment.OneOp oo, AppOpsManager aom, int op,
                PackageManager pm) {
            List<MyAppEntry> list = new ArrayList<MyAppEntry>();
            int mode;
            String pkg = "";
            for (int i = 0; i < oo.pkgs.size(); ++i) {
                pkg = oo.pkgs.get(i);
                mode = aom.checkOp(op, Util.getUidForPkg(pm, pkg), pkg);
                MyAppEntry mae = new MyAppEntry(pkg, mode);
                list.add(mae);
            }
            return list;
        }
    }
}
