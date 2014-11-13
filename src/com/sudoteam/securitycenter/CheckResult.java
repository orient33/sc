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
    public static final int TYPE_PASSED = 0;
    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_AUTO = 2;
}
