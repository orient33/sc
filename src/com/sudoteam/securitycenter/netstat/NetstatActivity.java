package com.sudoteam.securitycenter.netstat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class NetstatActivity extends Activity implements View.OnClickListener {

    String title;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        title = getString(R.string.module_net);
        setContentView(R.layout.activity_netstat);
        if (b == null) {
            Fragment l = new NetFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.net_container, l).commit();
        }
        Util.setActionBar(this, true, title, null);
    }

    @Override
    public void onClick(View v) {
    }

    class NetFragment extends Fragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_net, null);
            int ids[] = {R.id.data_usage_day,R.id.data_usage_month,R.id.data_usage_set,R.id.data_usage_night};
            for(int id : ids)
                v.findViewById(id).setOnClickListener(this);
            return v;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            int cycle = DataUsageListFragment.CYCLE_MONTH;
            Bundle bundle = new Bundle();
            switch (id) {
                case R.id.data_usage_day:
                    cycle = DataUsageListFragment.CYCLE_DAY;
                case R.id.data_usage_month:
                    Fragment f = new DataUsageListFragment();
                    bundle.putInt(DataUsageListFragment.CYCLE_KEY,cycle);
                    f.setArguments(bundle);
                    Util.replaceNewFragment(getActivity(), R.id.net_container, f);
                    break;
                case R.id.data_usage_set:
                case R.id.data_usage_night:
                    Toast.makeText(getActivity(),":TODO",0).show();
                    break;
            }

        }
    }

}
