package com.sudoteam.securitycenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

import com.sudoteam.securitycenter.checkitem.AdbCheck;
import com.sudoteam.securitycenter.checkitem.DefaultSmsCheck;
import com.sudoteam.securitycenter.checkitem.DeviceManagerCheck;
import com.sudoteam.securitycenter.checkitem.NonMarketAppsCheck;
import com.sudoteam.securitycenter.checkitem.RootCheck;
import com.sudoteam.securitycenter.netstat.CheckDataUsage;
import com.sudoteam.securitycenter.optimizer.CheckOptimizer;

public class OneKeyCheck {

    private final Context mContext;
    private final Handler mHandler;
    private final ArrayList<ICheck> mCheckList;

    public OneKeyCheck(Context c, Handler h) {
        mContext = c;
        mHandler = h;
        mCheckList = new ArrayList<ICheck>();
    }

    public List<CheckResult> checkAll() {
        ensureCheckList();
        List<CheckResult> list = new ArrayList<CheckResult>();
        for (ICheck ic : mCheckList) {
            CheckResult cr = ic.doCheck(mContext);
            list.add(cr);
            mHandler.obtainMessage(0, cr).sendToTarget();
            SystemClock.sleep(500);
            Util.i("" + cr);
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
        if (mCheckList.size() > 0)
            return;
        CheckDataUsage cdu = new CheckDataUsage();
        DeviceManagerCheck dmcTask = DeviceManagerCheck.getInstance(mContext);
        AdbCheck adbTask = new AdbCheck(mContext);
        NonMarketAppsCheck nonMarketTask = new NonMarketAppsCheck(mContext);
        DefaultSmsCheck smsTask = new DefaultSmsCheck(mContext);
        mCheckList.add(cdu);
        mCheckList.add(dmcTask);
        mCheckList.add(adbTask);
        mCheckList.add(nonMarketTask);
        mCheckList.add(smsTask);
        mCheckList.add(new CheckOptimizer());
        mCheckList.add(new RootCheck());
    }
}
