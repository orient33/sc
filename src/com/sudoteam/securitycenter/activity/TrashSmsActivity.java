package com.sudoteam.securitycenter.activity;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import com.sudoteam.securitycenter.R;

import com.sudoteam.securitycenter.manager.MessageManager;

import java.lang.Override;
import android.os.Handler;
import java.text.SimpleDateFormat;
import android.database.sqlite.SQLiteDatabase;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Table;

public class TrashSmsActivity extends Activity {

    public static Context context;

    public static DbUtils dbUtils = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        context = this;

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}


    public static class TrashSmsReceiver extends BroadcastReceiver {
    	
        @Override
        public void onReceive(final Context context, Intent intent) {

            dbUtils = DbUtils.create(context,"trash_sms.db");

            if(null != intent){

                Log.i("huayang","will show the trash sms : ");
                Log.i("huayang","number is  : " + intent.getCharSequenceExtra("num"));
                Log.i("huayang","body is  : " + intent.getCharSequenceExtra("body"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long time = intent.getLongExtra("time",0);
                String rev = "";
                if(time != 0)
                    rev = format.format(time);
                Log.i("huayang","time is  : " + rev);

                final Intent in = intent.getParcelableExtra("intent");

                if(in != null){
                    Log.i("huayang","---will insert to database");
                    MessageManager.getInstance(context).insertMessageToUserDatabase(dbUtils,in);
                }

            }
        }
    }


    /**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

            //TextView insert = (TextView)rootView.findViewById(R.id.insert);
//            insert.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    // TODO Auto-generated method stub
//                    MessageManager.getInstance(context).insertMessageToSystemDatabase();
//                }
//            });

			return rootView;
		}
	}

}
