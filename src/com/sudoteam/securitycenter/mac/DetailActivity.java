package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class DetailActivity extends Activity {

    final static String KEY_PKG = "key-pkg", KEY_OP = "key-op";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        String pkg = getIntent().getStringExtra(KEY_PKG);
        int op = getIntent().getIntExtra(KEY_OP, -1);
        final Fragment fragment;
        final String title;
        Bundle bundle = new Bundle();
        if (op != -1) { //  op detail
            fragment = new OpsDetail();
            bundle.putInt(OpsDetail.KEY, op);
            title = getString(R.string.ops_detail);
        } else {        //  fragment = // to app detail
            fragment = new AppDetail();
            bundle.putString(AppDetail.KEY, pkg);
            title = getString(R.string.app_detail);
        }
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        Util.setActionBar(this, true, title, null);
    }

}
