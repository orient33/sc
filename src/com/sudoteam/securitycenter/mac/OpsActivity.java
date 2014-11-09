package com.sudoteam.securitycenter.mac;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.sudoteam.securitycenter.Adapter.SlidePagerAdapter;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.Views.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * app ops man UI.
 */
public class OpsActivity extends FragmentActivity implements View.OnClickListener {
    private PagerSlidingTabStrip mTabs;
    AppOpsState mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ops);
        mState = new AppOpsState(this);
        Util.setActionBar(this, true, getTitle().toString(), this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.ops_tabs);
        ViewPager vp = (ViewPager) findViewById(R.id.ops_pagers);
        String t1 = getString(R.string.ops_app_list), t0 = getString(R.string.ops_ops_list);
        ArrayList<String> titles = new ArrayList<String>(2);
        titles.add(t0);
        titles.add(t1);
        vp.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
        // from AnnoyInterceptActivity hmm...
        tabs.setViewPager(vp);
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        tabs.setIndicatorColor(Color.parseColor("#8e2921"));
        tabs.setSelectedTextColor(Color.parseColor("#8e2921"));
        tabs.setTabBackground(0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_setting:
                Toast.makeText(this, "TO BE CONTINUE -):", 0).show();
                break;
        }
    }

    static class MyAdapter extends SlidePagerAdapter {
        private Fragment mmFragmeng0, mmFragment1;

        public MyAdapter(FragmentManager fm, List<String> titles) {
            super(fm, titles);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if (mmFragmeng0 == null) {
                        mmFragmeng0 = new OpsSummary();
                    }
                    return mmFragmeng0;

                case 1:
                    if (mmFragment1 == null) {
                        mmFragment1 = new OpsListFragment();
                    }
                    return mmFragment1;

                default:
                    return null;
            }

        }
    }
}