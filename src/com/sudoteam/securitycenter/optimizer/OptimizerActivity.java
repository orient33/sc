package com.sudoteam.securitycenter.optimizer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class OptimizerActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        if (s == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new OptFragment()).commit();
        }
        String title = getString(R.string.module_optimizer);
        Util.setActionBar(this, true, title, this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.actionbar_setting:
                Toast.makeText(this,"set optimizer",0).show();
                break;
        }
    }


}
