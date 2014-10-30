package com.sudoteam.securitycenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.sudoteam.securitycenter.netstat.NetstatActivity;
import com.sudoteam.securitycenter.optimizer.OptimizerActivity;
import com.sudoteam.securitycenter.Activity.*;

public class MainActivity extends Activity {

    private static final int[] BUTTON_IDS = {R.id.module_optimizer, R.id.module_net, R.id.module_block,
            R.id.module_save, R.id.module_antivirse, R.id.module_mac, R.id.one_key_check};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        if (b == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new _Frag()).commit();
        }
//		ActionBar ab = getActionBar();
//		if (ab != null)
//			ab.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
//			replaceNewFragment(OpsSummary.getOpsSummary());
        } else if (id == R.id.isRoot) {
            Util.isRooted(this);
        }
        return true;
    }

    public static class _Frag extends MyFragment implements View.OnClickListener {

        public _Frag() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_main, container, false);
            for (int id : BUTTON_IDS) {
                Button btn = (Button) v.findViewById(id);
                btn.setOnClickListener(this);
            }
            return v;
        }


		@Override
		public void onClick(View v) {
			final MainActivity ma = (MainActivity) getActivity();
			int id = v.getId();
			if(id == R.id.one_key_check){
				Toast.makeText(ma, "hmm...", Toast.LENGTH_SHORT).show();
			}else if (id == R.id.module_optimizer) {
				ma.startActivity(new Intent(ma, OptimizerActivity.class));
			}else if(id == R.id.module_mac){
//				ma.startActivity(new Intent(ma, OpsActivity.class));
			}else if(id == R.id.module_net){
				ma.startActivity(new Intent(ma, NetstatActivity.class));
			}else if(id == R.id.module_antivirse){
				ma.startActivity(new Intent(ma,ScanVirusActivity.class));
			}else {
                Toast.makeText(ma, "to be continue...", Toast.LENGTH_SHORT).show();
            }
		}
	}

}
