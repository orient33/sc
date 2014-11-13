package com.sudoteam.securitycenter;

import android.content.Context;

/**
 */
public interface ICheck {
    /**
     * the item ,which need check, must implement this interface.
     */
    CheckResult doCheck(Context context);
}
