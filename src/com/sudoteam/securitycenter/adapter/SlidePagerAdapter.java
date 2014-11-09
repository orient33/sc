package com.sudoteam.securitycenter.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by huayang on 14-10-31.
 *
 * just extends this & give a constructure ,override getItem method
 */
public class SlidePagerAdapter extends FragmentPagerAdapter {

    private List<String> titles ;

    public SlidePagerAdapter(FragmentManager fm, List<String> titles) {

        super(fm);
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Fragment getItem(int position) {

        return null;
    }
}
