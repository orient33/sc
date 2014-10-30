package com.sudoteam.securitycenter.netstat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 流量使用的图标统计图 如折线图
 */
public class DataUsageChart extends Fragment {

    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b){
//        View v = li.inflate(R.layout.data_usage_chart, null);

        TextView tv = new TextView(getActivity());
        tv.setTextSize(50);
        tv.setText("TODO "+getArguments().getString("key"));
        return tv;
    }
}
