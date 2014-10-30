package com.sudoteam.securitycenter.netstat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class NetstatActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.activity_netstat);
        if (b == null) {
            Fragment l = new NetFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.net_container, l).commit();
        }
        getActionBar().hide();
    }

    class NetFragment extends Fragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_net, null);
            v.findViewById(R.id.net_use1).setOnClickListener(this);
            v.findViewById(R.id.net_use2).setOnClickListener(this);
            return v;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch(id){
                case R.id.net_use1:
                    Fragment f = new DataUsageListFragment();
                    Util.replaceNewFragment(getActivity(),R.id.net_container,f);
                    break;
                case R.id.net_use2:
                    getActivity().startActivity(new Intent(getActivity(),NetFirewallActivity.class));
                    break;
            }

        }
    }

}
