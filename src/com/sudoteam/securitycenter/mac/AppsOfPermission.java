package com.sudoteam.securitycenter.mac;
import com.sudoteam.securitycenter.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class AppsOfPermission extends Activity {
	private static final String TAG = "AppsOfPermission";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_of_permission);
		
		AppsUsingCertainOpFragment fragment = new AppsUsingCertainOpFragment(getIntent().getIntExtra("op", -1));
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fr = fm.beginTransaction();
		fr.replace(R.id.frame, fragment);
		fr.commit();
	}
}