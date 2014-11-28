
package com.sudoteam.securitycenter.mac;

import static com.sudoteam.securitycenter.mac.MacUtil.modeToPosition;
import static com.sudoteam.securitycenter.mac.MacUtil.positionToMode;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.mac.AppListFragment.OneApp;
import com.sudoteam.securitycenter.mac.AppListFragment.OpMode;

import java.util.Collections;
import java.util.List;

/**
 * detail information about one application's ops.
 */
public class AppDetail extends Fragment {
    String mPkgName;
    int mUid;
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
        mPkgName = getArguments().getString(DetailActivity.KEY_PKG);
        mUid = getArguments().getInt(DetailActivity.KEY_UID);
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
        mAdapter = new OpAdapter(mAct, mOneApp.getOpsSwitches());
        mListView.setAdapter(mAdapter);
        return root;
    }

    class OpAdapter extends BaseAdapter {
        final List<OpMode> mmOps;
        final LayoutInflater mmLi;

        OpAdapter(Context c, List<OpMode> data) {
            mmLi = LayoutInflater.from(c);
            MacUtil.getLabelForOp(c, 0);// to init string..
            MacUtil.getDescriptForOp(c, 0);
            MacUtil.getLabelForMode(c, 0);
            Collections.sort(data);
            mmOps = data;
        }

        void modifyOneOp(int op, int mode) {
            for (OpMode moe : mmOps) {
                if (moe.getOp() == op) {
                    moe.changeMode(mode);
                    return;
                }
            }
        }

        @Override
        public int getCount() {
            return mmOps == null ? 0 : mmOps.size();
        }

        @Override
        public OpMode getItem(int i) {
            return mmOps == null ? null : mmOps.get(i);
        }

        @Override
        public long getItemId(int p) {
            return 0;
        }

        @Override
        public View getView(int p, View v, ViewGroup par) {
            final OpMode oe = getItem(p);
            // note: this view must create every time, see AppOpsDetails.java in Settings.apk
            v = mmLi.inflate(R.layout.ops_app_detail_item, null);
            final String label = MacUtil.getLabelForOp(null, oe.getOp());
            TextView name = (TextView) v.findViewById(R.id.ops_app_detail_op_name);
            name.setText(label);
            TextView summ = (TextView) v.findViewById(R.id.ops_app_detail_ops);
            StringBuilder sb = new StringBuilder();
            for (Integer code : oe.opList) {
                sb.append(MacUtil.getDescriptForOp(null, code) +",");
            }
            sb.deleteCharAt(sb.length()-1);
            final String summary = sb.toString();
            if (!summary.equals(label))
                summ.setText(summary);
            Switch sw = (Switch) v.findViewById(R.id.ops_app_detail_sw);
            Spinner sp = (Spinner) v.findViewById(R.id.ops_app_detail_sp);
            sw.setVisibility(View.INVISIBLE);
            sp.setVisibility(View.INVISIBLE);
            final int switchOp = AppOpsManager.opToSwitch(oe.getOp());
            final int mode = oe.getMode();

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
                    MacUtil.setMyMode(switchOp, mUid, mPkgName, positionToMode(position), mAom);
                    modifyOneOp(switchOp, positionToMode(position));
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
                    MacUtil.setMyMode(switchOp, mUid, mPkgName, mmode, mAom);
                    modifyOneOp(switchOp, mmode);
                }
            });

            if (AppOpsManager.isStrictOp(switchOp)) {
                sp.setVisibility(View.VISIBLE);
            } else {
                sw.setVisibility(View.VISIBLE);
            }
            return v;
        }
    }
}
