package com.sudoteam.securitycenter.optimizer;

import android.content.Context;
import android.text.TextUtils;

import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * white list of task/app .
 */

public class KillProcessWhiteList {

    static KillProcessWhiteList sInit;
    private final OptDatabase mDb;
    private boolean mInited = false;
    private final List<String> mList = new ArrayList<String>();

    private KillProcessWhiteList(Context c) {
        mDb = OptDatabase.get(c);
    }

    static KillProcessWhiteList get(Context c) {
        if (sInit == null)
            sInit = new KillProcessWhiteList(c);
        return sInit;
    }

    private void ensureInit() {
        if (mInited)
            return;
        mList.clear();
        mList.addAll(mDb.getTaskList());
        mInited = true;
    }

    /**
     * 判断一个task是否在白名单
     */
    public boolean isWhite(String name) {
        ensureInit();
        if (TextUtils.isEmpty(name)) return false;
        for (String item : mList) {
            if (name.equals(item))
                return true;
        }
        return false;
    }

    /**
     * return whether the app is in white list
     */
    public boolean add(String name) {
        Util.i("add task : " + name);
        if (isWhite(name)) return true;
        mList.add(name);
        mDb.addTaskWhiteList(name);
        return true;
    }

    public boolean remove(String name) {
        Util.i("remove task : " + name);
        if (!isWhite(name)) return true;
        int index, size = mList.size();
        for (index = 0; index < size; ++index) {
            if (mList.get(index).equals(name)){
                break;
            }
        }
        if(index < size)
            mList.remove(index);
        return mDb.removeTaskWhiteList(name);
    }
}
