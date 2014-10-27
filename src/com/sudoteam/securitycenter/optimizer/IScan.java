package com.sudoteam.securitycenter.optimizer;

import android.os.Handler;

public interface IScan {
    int doCheck(Handler h);
    int getCurrentCount();
    void destoryResult();
    int optimizeSelect(boolean killAll);
    boolean isSelectAll();
    void select(boolean now);
}
