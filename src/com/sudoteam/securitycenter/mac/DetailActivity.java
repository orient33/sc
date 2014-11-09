package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.os.Bundle;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class DetailActivity extends Activity {

    final static String KEY = "key-for";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        String pkg = getIntent().getStringExtra(KEY);
        if (b == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new AppOpsDetails(pkg)).commit();
        }
    }

}
