package com.sudoteam.securitycenter.mac;
import com.sudoteam.securitycenter.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class PermissionsOfApp extends Activity {
	TextView txt_perms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permissions_of_app);

		// 步骤一：添加一个FragmentTransaction的实例
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 步骤二：用add()方法加上Fragment的对象rightFragment
		Bundle packName = getIntent().getBundleExtra("packname");
		AppOpsDetails appOpsDetails = new AppOpsDetails(packName);
		transaction.add(R.id.details, appOpsDetails);
		// 步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
		transaction.commit();
	}

}
