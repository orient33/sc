package com.sudoteam.securitycenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudoteam.securitycenter.Activity.AnnoyInterceptActivity;
import com.sudoteam.securitycenter.activity.PowerManagerActivity;
import com.sudoteam.securitycenter.Activity.ScanVirusActivity_v2;
import com.sudoteam.securitycenter.mac.OpsActivity;
import com.sudoteam.securitycenter.netstat.NetstatActivity;
import com.sudoteam.securitycenter.optimizer.OptimizerActivity;

public class MainActivity extends Activity {

    private static final int[] BUTTON_IDS = {R.id.module_optimizer, R.id.module_net, R.id.module_block,
            R.id.module_save, R.id.module_antivirse, R.id.module_mac};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        if (b == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new _Frag()).commit();
        }
        String title = getString(R.string.app_name);
        Util.setActionBar(this, false, title, null);
        final OneKeyCheck okc = new OneKeyCheck(this);
        new Thread(){
            @Override
            public void run(){
                okc.checkAll();
            }
        }.start();
    }

    public static class _Frag extends MyFragment implements View.OnClickListener {

        public _Frag() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_main, container, false);
            for (int id : BUTTON_IDS) {
                View btn = v.findViewById(id);
                btn.setOnClickListener(this);
            }
            return v;
        }


		@Override
		public void onClick(View v) {
			final MainActivity ma = (MainActivity) getActivity();
			int id = v.getId();
			if (id == R.id.module_optimizer) {
				ma.startActivity(new Intent(ma, OptimizerActivity.class));
			}else if(id == R.id.module_mac){
				ma.startActivity(new Intent(ma, OpsActivity.class));
			}else if(id == R.id.module_net){
				ma.startActivity(new Intent(ma, NetstatActivity.class));
			}else if(id == R.id.module_antivirse){
				ma.startActivity(new Intent(ma,ScanVirusActivity_v2.class));
			}else if(id == R.id.module_block){
				ma.startActivity(new Intent(ma, AnnoyInterceptActivity.class));
			}else {
				ma.startActivity(new Intent(ma, PowerManagerActivity.class));
            }
		}
	}

}
