package com.sudoteam.securitycenter.netstat;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class NetstatActivity extends Activity implements View.OnClickListener {

    String title;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        title = getString(R.string.module_net);
        setContentView(R.layout.activity_netstat);
        if (b == null) {
            Fragment l = new NetFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.net_container, l).commit();
        }
        Util.setCustomTitle(this, true, title, null);
    }

    @Override
    public void onClick(View v) {
    }

    class NetFragment extends Fragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_net, null);
            int ids[] = {R.id.data_usage_day,R.id.data_usage_month,R.id.data_usage_set,R.id.data_usage_mac};
            for(int id : ids)
                v.findViewById(id).setOnClickListener(this);
            WaterWaveProgress wwp = (WaterWaveProgress)v.findViewById(R.id.net_anim);
            wwp.animateWave();
            return v;
        }
        @Override
        public void onResume(){
            super.onResume();
            Util.updateActionBarTitle(getActivity(),title);
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
                case R.id.data_usage_mac:
                    Fragment mac = new NetMacFragment();
                    Util.replaceNewFragment(getActivity(),R.id.net_container, mac);
                    break;
                case R.id.data_usage_set:
                    new NetSettingFragment().show(getFragmentManager(),"set");
                    break;
            }

        }
    }

}
