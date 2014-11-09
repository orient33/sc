package com.sudoteam.securitycenter.optimizer;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.optimizer.KillProcessAdapter.KillItem;

public class KillProcessDetail extends Fragment {
    final KillItem mItem;
    Activity mActivity;
    ImageView mIcon;
    TextView mName, mSize;
    Switch mSwitch;
    Button mOpWhite, mOpKill;

    KillProcessDetail(KillItem ki, BaseAdapter adapter) {
        mItem = ki;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.kill_process_detail, null);
        mIcon = (ImageView) v.findViewById(R.id.process_detail_icon);
        mName = (TextView) v.findViewById(R.id.process_detail_title);
        mSize = (TextView) v.findViewById(R.id.process_detail_size);
        mSwitch = (Switch) v.findViewById(R.id.process_detail_white);
        mOpKill = (Button) v.findViewById(R.id.process_detail_op_kill);
        mOpWhite = (Button) v.findViewById(R.id.process_detail_op_white);

        mIcon.setImageDrawable(mItem.icon);
        mName.setText(mItem.title);
        mSize.setText(mItem.memUse);
        mSwitch.setChecked(mItem.inWhite);
//        mOpWhite /mSwitch
        mSwitch.setOnCheckedChangeListener(lis);
        return v;
    }

    private CompoundButton.OnCheckedChangeListener lis = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton bv, boolean isChecked) {
            final KillProcessWhiteList whiteList = KillProcessWhiteList.get(mActivity);
            new AsyncTask<KillItem, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(KillItem... ks) {
                    final KillItem ki = ks[0];
                    boolean r;
                    if (ki.inWhite)
                        r = whiteList.remove(ki.appInfo.processName);
                    else
                        r = whiteList.add(ki.appInfo.processName);
                    SystemClock.sleep(100);
                    return r;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    if (success) {
                        Toast.makeText(mActivity, R.string.op_success, Toast.LENGTH_SHORT).show();
                        mItem.inWhite = !mItem.inWhite;
                    } else {
                        Toast.makeText(mActivity, R.string.op_fail, Toast.LENGTH_SHORT).show();
                        bv.setChecked(mItem.inWhite);
                    }
                }
            }.execute(mItem);
        }
    };
}
