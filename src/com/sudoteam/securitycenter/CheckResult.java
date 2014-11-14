package com.sudoteam.securitycenter;

import android.content.Context;

/**
 * the result of one-key-check
 */
public class CheckResult {
    public static interface IFix {
        boolean doFix(Context context);
    }

    public String name;
    public String content;
    public int type;
    public IFix callback;
    /**
     * the item is health/OK
     */
    public static final int TYPE_PASSED = 0;    // pass
    public static final int TYPE_MANUAL = 1;    // need fix this manually
    public static final int TYPE_AUTO = 2;      // can fix automatically
    public static final int TYPE_CANNO_FIX = 3; // can not fix, what we can do is just notice user.

    @Override
    public String toString() {
        return "{CheckResult} name=" + name + ",content=" + content + ",type=" + type;
    }
}
