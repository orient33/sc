package com.sudoteam.securitycenter.optimizer;

import android.os.Handler;

public interface IScan {
    int doCheck(Handler h, int what);
    int getCurrentCount();
    void destoryResult();
    int optimizeSelect(boolean killAll);
    boolean isSelectAll();
    void select(boolean now);
}
