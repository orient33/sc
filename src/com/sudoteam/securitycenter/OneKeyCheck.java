package com.sudoteam.securitycenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.sudoteam.securitycenter.checkitem.AdbCheck;
import com.sudoteam.securitycenter.checkitem.DefaultSmsCheck;
import com.sudoteam.securitycenter.checkitem.DeviceManagerCheck;
import com.sudoteam.securitycenter.checkitem.NonMarketAppsCheck;
import com.sudoteam.securitycenter.netstat.CheckDataUsage;
import com.sudoteam.securitycenter.optimizer.CheckOptimizer;

/**
 */
public class OneKeyCheck {

    private final Context mContext;
    private final ArrayList<ICheck> mCheckList;

    public OneKeyCheck(Context c) {
        mContext = c;
        mCheckList = new ArrayList<ICheck>();
    }

    public List<CheckResult> checkAll() {
        ensureCheckList();
        List<CheckResult> list = new ArrayList<CheckResult>();
        for (ICheck ic : mCheckList) {
            CheckResult cr = ic.doCheck(mContext);
            list.add(cr);
            Util.i("" + cr);
//        :TODO notify listview / adapter data changed.
        }
        return list;
    }

    public void fix(List<CheckResult> lists) {
        final Context context = mContext;
        for (CheckResult cr : lists) {
            cr.callback.doFix(context);
        }
    }

    private void ensureCheckList() {
        if (mCheckList.size() > 0) return;
//        mCheckList.add();//:TODO add all ICheck, which will be check.
        CheckDataUsage cdu = new CheckDataUsage();
        DeviceManagerCheck dmcTask = DeviceManagerCheck.getInstance(mContext);
        AdbCheck adbTask =  new AdbCheck();
        NonMarketAppsCheck nonMarketTask = new NonMarketAppsCheck();
        DefaultSmsCheck smsTask = new DefaultSmsCheck(mContext);
        mCheckList.add(cdu);
        mCheckList.add(dmcTask);
        mCheckList.add(adbTask);
        mCheckList.add(nonMarketTask);
        mCheckList.add(smsTask);
        mCheckList.add(new CheckOptimizer());
    }
}







