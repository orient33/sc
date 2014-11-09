package com.sudoteam.securitycenter.Activity;

import android.app.Activity;
import android.os.Bundle;
import com.sudoteam.securitycenter.R;

import com.sudoteam.securitycenter.Util;


public class AnnoyInterruptSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annoy_interrupt_settings);

        Util.setActionBar(this, true, "骚扰拦截",0,null);


    }

}
