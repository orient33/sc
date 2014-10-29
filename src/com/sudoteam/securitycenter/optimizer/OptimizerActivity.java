package com.sudoteam.securitycenter.optimizer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sudoteam.securitycenter.R;

public class OptimizerActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        if (s == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new OptFragment()).commit();
        }
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
    }


}
