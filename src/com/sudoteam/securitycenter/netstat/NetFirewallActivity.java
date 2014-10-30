package com.sudoteam.securitycenter.netstat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sudoteam.securitycenter.R;

/**
 * 流量排行以及流量控制 流量统计表格
 */
public class NetFirewallActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_firewall);
        ViewPager vp = (ViewPager)findViewById(R.id.net_viewpager);
        final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                Fragment f = null;
                Bundle b = new Bundle();
                if(i == 1)
                    f = new DataUsageChart();
                else
                    f = new DataUsageChart();
                b.putString("key","第 "+ i + "页");
                f.setArguments(b);
                return f;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        vp.setAdapter(adapter);
    }


}