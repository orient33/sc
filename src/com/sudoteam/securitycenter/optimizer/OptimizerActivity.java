package com.sudoteam.securitycenter.optimizer;

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
            getFragmentManager().beginTransaction().add(R.id.container, new OptimizerFragment()).commit();
        }
        String title = getString(R.string.module_optimizer);
        Util.setActionBar(this, true, title, R.drawable.optimizer_set, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_setting:
                Toast.makeText(this, "To Be Continue..", 0).show();
                break;
        }
    }
}
