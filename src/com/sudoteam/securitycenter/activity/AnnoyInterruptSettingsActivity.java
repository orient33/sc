package com.sudoteam.securitycenter.activity;

import android.os.Bundle;

import com.sudoteam.securitycenter.BaseActivity;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;


public class AnnoyInterruptSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annoy_interrupt_settings);

        Util.setCustomTitle(this, true, "骚扰拦截",0,null);


    }

}
