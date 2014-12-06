package com.sudoteam.securitycenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sudoteam.securitycenter.activity.AnnoyInterceptActivity;
import com.sudoteam.securitycenter.activity.PowerManagerActivity;
import com.sudoteam.securitycenter.activity.ScanVirusActivity;
import com.sudoteam.securitycenter.entity.ItemData;
import com.sudoteam.securitycenter.mac.OpsActivity;
import com.sudoteam.securitycenter.netstat.NetstatActivity;
import com.sudoteam.securitycenter.optimizer.OptimizerActivity;
import com.sudoteam.securitycenter.views.LineView;

public class MainActivity extends BaseActivity {

    private static final int[] BUTTON_IDS = {R.id.module_optimizer, R.id.module_net, R.id.module_block,
            R.id.module_save, R.id.module_antivirse, R.id.module_mac, R.id.title_img};

    private static RelativeLayout menuParent,checkList;
    
    private static Animation exit ,enter;
    
    private static TextView titleText;
    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        if (b == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new _Frag()).commit();
        }
        String title = getString(R.string.app_name);
        Util.setCustomTitle(this, false, title, null);
        
         exit = AnimationUtils.loadAnimation(this, R.anim.dialog_exit_anim);
         enter = AnimationUtils.loadAnimation(this,R.anim.dialog_enter_anim);
//        final OneKeyCheck okc = new OneKeyCheck(this);
//        new Thread(){
//            @Override
//            public void run(){
//                okc.checkAll();
//            }
//        }.start();
    }

    public static class _Frag extends MyFragment implements View.OnClickListener,LineView.OnItemClickListener {
 
        private LineView itemContainer;
        private ScrollView scrollView;
        
        private Handler handler = new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		switch(msg.what){
        		case 0:
        			CheckResult cr = (CheckResult)msg.obj;
        			createListItem(false,cr.content);
        			break;
        		}
        	}
        };
        
        public _Frag() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_main, container, false);

            titleText = (TextView)v.findViewById(R.id.one_key_check_title);

            menuParent = (RelativeLayout)v.findViewById(R.id.menu_parent);
            checkList = (RelativeLayout)v.findViewById(R.id.check_list);
            
            itemContainer = (LineView)v.findViewById(R.id.one_key_item_container);
            scrollView = (ScrollView)v.findViewById(R.id.one_key_checked_list);
            itemContainer.setOnItemClickedListener(this);
            
            
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
				ma.startActivity(new Intent(ma,ScanVirusActivity.class));
			}else if(id == R.id.module_block){
				ma.startActivity(new Intent(ma, AnnoyInterceptActivity.class));
			}else if(id == R.id.module_save){
				ma.startActivity(new Intent(ma, PowerManagerActivity.class));
            }else if(id == R.id.title_img){
                prepareForOneKeyCheck();
                doCheck();
            }

		}
		
		@Override
		public void onItemClick(View view,Object data ,int position){
			
			android.util.Log.i("position","p = " + position);
		}


        private void prepareForOneKeyCheck(){

            titleText.setText("检查中...");
            menuParent.setVisibility(View.INVISIBLE);
            checkList.setVisibility(View.VISIBLE);

            menuParent.setAnimation(exit);
            exit.start();

            
            checkList.setAnimation(enter);
            enter.start();
            
            itemContainer.removeAllViews();
        }
        
		private void doCheck() {
			final OneKeyCheck okc = new OneKeyCheck(getActivity(), handler);
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					okc.checkAll();
				}
			}).start();

		}
        
        
        private void createListItem(boolean isLast,String name){

            //itemContainer.setProblemView(ViewUtils.getView(getActivity(),R.layout.test_view));

            ItemData data = itemContainer.createAData(isLast);
            data.setTitle(name);
            itemContainer.addViewByAnimation(data,R.layout.one_key_check_item);

            /**
             * moving the bottom item up in UI thread
             */
            handler.post(new Runnable() {
                @Override
                public void run() {

                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
        
       
        
	}
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	
    	if(checkList.getVisibility() == View.VISIBLE){
    		
            titleText.setText("一键体检");
            menuParent.setVisibility(View.VISIBLE);
            checkList.setVisibility(View.INVISIBLE);

            menuParent.setAnimation(enter);
            exit.start();
            
            checkList.setAnimation(exit);
            enter.start();
            
            return;
    	}
    	
    	super.onBackPressed();
    }

}
