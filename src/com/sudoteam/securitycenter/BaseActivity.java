package com.sudoteam.securitycenter;

import android.os.Bundle;
import android.view.Window;

public class BaseActivity extends android.support.v4.app.FragmentActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }
}
