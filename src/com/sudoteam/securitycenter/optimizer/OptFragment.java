package com.sudoteam.securitycenter.optimizer;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sudoteam.securitycenter.MyFragment;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class OptFragment extends MyFragment implements View.OnClickListener {

    private final int ids[] = {R.id.opt_garbage, R.id.opt_task, R.id.opt_selfon, R.id.opt_set};

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.opt_fragment, null);
        for (int id : ids)
            v.findViewById(id).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.opt_garbage:
                Util.replaceNewFragment(getActivity(), R.id.container, new ClearFragment());
                break;
            case R.id.opt_task:
                Util.replaceNewFragment(getActivity(), R.id.container, new KillProcessFragment());
                break;
            case R.id.opt_selfon:
                Util.replaceNewFragment(getActivity(), R.id.container, new SelfStartFragment());
                break;
            case R.id.opt_set:
                Toast t = Toast.makeText(getActivity(), "To Be Continue..", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
        }
    }
}
