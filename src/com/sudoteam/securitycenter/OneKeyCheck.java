package com.sudoteam.securitycenter;

import android.content.Context;

import com.sudoteam.securitycenter.checkitem.DeviceManagerCheck;
import com.sudoteam.securitycenter.netstat.CheckDataUsage;

import java.util.ArrayList;
import java.util.List;

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
            Util.i(""+cr);
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
        DeviceManagerCheck dmc = DeviceManagerCheck.getInstance(mContext);
        mCheckList.add(cdu);
        mCheckList.add(dmc);
    }
}
