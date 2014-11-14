package com.sudoteam.securitycenter.netstat;

import android.content.Context;
import android.content.Intent;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.format.Formatter;

import com.sudoteam.securitycenter.CheckResult;
import com.sudoteam.securitycenter.ICheck;
import com.sudoteam.securitycenter.R;

/**
 */
public class CheckDataUsage implements ICheck {

    @Override
    public CheckResult doCheck(Context context) {
        final CheckResult cr = new CheckResult();
        final String title1 = context.getString(R.string.data_sum),
                title2 = context.getString(R.string.data_used);
        cr.name = title1 + "/" + title2;
        NetUtils.i("One-Key-Check  start check : " + cr.name);
        long all = NetUtils.getDataAllSize(context);
        if (all < 0) { // not set data-all-size.
            cr.content = context.getString(R.string.not_set_data_all);
            cr.type = CheckResult.TYPE_MANUAL;
            cr.callback = new CheckResult.IFix() {
                @Override
                public boolean doFix(Context context) {
                    Intent intent = new Intent(context, NetstatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return false; // return true if fixed. may be startActivityForResult() is better.
                }
            };
        }else{
            long used = getUsedDataSize(context);
            NetUtils.i("all size : "+ Formatter.formatFileSize(context,all)+", used: "+Formatter.formatFileSize(context,used));
            if(used >= all){    //
                cr.type = CheckResult.TYPE_CANNO_FIX;
                cr.content = context.getString(R.string.data_usage_above_all);
            }else {
                cr.type = CheckResult.TYPE_PASSED;
                cr.content = context.getString(R.string.seted_data_all);
            }
        }
        return cr;
    }

    private long getUsedDataSize(Context c) {
        INetworkStatsService iss = INetworkStatsService.Stub.asInterface(ServiceManager
                .getService(Context.NETWORK_STATS_SERVICE));
        INetworkStatsSession inss = null;
        try {
            inss = iss.openSession();
            final long now = System.currentTimeMillis();
            long start = NetUtils.getStartForMonth(now);
            NetworkTemplate nt = NetworkTemplate.buildTemplateWifiWildcard();//:TODO
            NetworkStats ns = inss.getSummaryForNetwork(nt, start, now);
            return ns.getTotalBytes();
        } catch (RemoteException e) {

        } finally {
            try {
                if (inss != null)
                    inss.close();
            } catch (RemoteException e) {
            }
        }
        return 0;
    }
}
