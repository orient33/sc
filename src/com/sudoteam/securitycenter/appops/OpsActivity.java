package com.sudoteam.securitycenter.appops;

import com.sudoteam.securitycenter.R;

import android.app.Activity;
import android.os.Bundle;

public class OpsActivity extends Activity {
	@Override
	protected void onCreate(Bundle s) {
		super.onCreate(s);
		setContentView(R.layout.activity_main);

		if (s == null) {
			getFragmentManager().beginTransaction().add(R.id.container,OpsSummary.getOpsSummary()).commit();
		}
	}


}
