package com.sudoteam.securitycenter.netstat;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.sudoteam.securitycenter.R;

public class NetstatActivity extends Activity {

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);

		setContentView(R.layout.activity_netstat);
		if (b == null) {
			Fragment l = new DataUsageList();
			getFragmentManager().beginTransaction()
				.add(R.id.net_container, l).commit();
		}
	}

}
