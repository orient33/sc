package com.sudoteam.securitycenter.netstat;

import android.net.NetworkStatsHistory;

public class ChartData {
    public NetworkStatsHistory network;

    public NetworkStatsHistory detail;	//when one app, show detail (= detailDefault + detailForground)
    public NetworkStatsHistory detailDefault;
    public NetworkStatsHistory detailForeground;
}
