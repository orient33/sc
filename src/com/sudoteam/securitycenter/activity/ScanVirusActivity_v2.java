package com.sudoteam.securitycenter.Activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Intent;

import com.sudoteam.securitycenter.Entity.ItemData;
import com.sudoteam.securitycenter.Views.LineView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sudoteam.securitycenter.Entity.ScanLog;
import com.sudoteam.securitycenter.Entity.ScanProcess;
import com.sudoteam.securitycenter.Manager.*;
import com.sudoteam.securitycenter.Views.SingleClickButton;
import com.sudoteam.securitycenter.R;

import java.util.ArrayList;
import java.util.List;



public class ScanVirusActivity_v2 extends SuperActivity implements
								View.OnClickListener ,ScanVirusManager.ScanPackageListener {

    public static String TAG = "MainActivity";
    public static String DB = "security.db";
    
    private LineView itemContainer;
    ScrollView scrollView;
    Handler handler = new Handler();
    private RelativeLayout layout,scanOverHealth;

    private Button scanMask;
    private Button appIcon;
    private ProgressBar progressBar;
    private TextView scaningAppName;
    private Animation zoomOut,zoomIn,ra;

    private Button scanLogList;
    private DbUtils dbUtils;
    private int scanCnt=0;
    private int virusCnt = 0;
    private int warnings = 0;

    private long createLogTime = 0;
    private ScanLog log ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_scan_virus);

        zoomOut = AnimationUtils.loadAnimation(this,R.anim.zoom_out);
        zoomIn = AnimationUtils.loadAnimation(this,R.anim.zoom_in);

        layout = (RelativeLayout) findViewById(R.id.scan_page_parent_scanning);
        layout.setOnClickListener(this);
        appIcon = (Button) findViewById(R.id.scan_app_icon);
        progressBar = (ProgressBar) findViewById(R.id.scan_progress);
        scaningAppName = (TextView) findViewById(R.id.current_scaning_package_name);
        scanOverHealth = (RelativeLayout) findViewById(R.id.scan_page_parent_over);

        scanLogList = (Button)findViewById(R.id.scan_log_list);
        scanLogList.setOnClickListener(this);

        scanMask = (Button) findViewById(R.id.scan_mask);
        ra = AnimationUtils.loadAnimation(this,R.anim.scan_mask_rotate);
        ra.setInterpolator(new LinearInterpolator());
        scanMask.setAnimation(ra);
        ra.start();

        itemContainer = (LineView)findViewById(R.id.item_container);
        scrollView = (ScrollView)findViewById(R.id.checked_list);

        dbUtils = DbUtils.create(this,DB);

        new Thread(new Runnable() {
            @Override
            public void run() {

                scanPackages();
            }
        }).start();


    }

    private void scanPackages(){

        final List<PackageInfo> infos = getPackageManager().getInstalledPackages(0);
        final List<PackageInfo> userInfos = new ArrayList<PackageInfo>();

        for(final PackageInfo info : infos){
            if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                userInfos.add(info);
            }
        }

        progressBar.setMax(userInfos != null ? userInfos.size():0);

        for(final PackageInfo info : userInfos){

            Log.i("Tag","name = " + info.applicationInfo.loadLabel(getPackageManager()));

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    appIcon.setBackground(info.applicationInfo.loadIcon(getPackageManager()));
                    appIcon.setAnimation(zoomOut);
                    zoomOut.start();

                    scaningAppName.setText("正在扫描 : " + info.applicationInfo.loadLabel(getPackageManager()));
                    progressBar.setProgress(++scanCnt);
                    createList(scanCnt==userInfos.size(),info.applicationInfo.loadLabel(getPackageManager()).toString());

                    if(scanCnt == userInfos.size()) {
                        Log.i("Tag","infos.size() = " + infos.size() + ", i = " + scanCnt);

                        scanMask.clearAnimation();
                        ra.cancel();
                        scanMask.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.INVISIBLE);
                        scanOverHealth.setVisibility(View.VISIBLE);

                        layout.setAnimation(zoomIn);
                        zoomIn.start();

                        scanOverHealth.setAnimation(zoomOut);
                        zoomOut.start();
                    }
                }
            });
        }

    }

    private void createList(boolean isLast,String name){
        //itemContainer.setProblemView(ViewUtils.getView(getActivity(),R.layout.test_view));
        ItemData data = itemContainer.createAData(isLast);
        data.setTitle(name);
        itemContainer.addViewByAnimation(data);

        handler.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.scan_page_parent_scanning :

                break;

            case R.id.scan_log_list:

                startActivity(new Intent(this,ScanLogActivity.class));
                //MessageManager mm = MessageManager.getInstance(this);
                break;
        }
    }

    /**
     * scan over and do something ...
     */
    private void overScanAndClear() {

        log.setClears(10201);

        try {
            dbUtils.update(log, WhereBuilder.b().expr("time = " + createLogTime),"clears");
        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    /**
     * start scan packages
     */
    private void startScan(){

        /**
         * each scan action will create a piece of log in db
         */
        log = new ScanLog();

        ScanVirusManager.getInstance(this)
                .setOnPackageScanedListener(this).scanPackages();

    }

    /**
     * save data to db
     * @param params
     * @param log
     */
    private void saveLogMsg(ScanProcess params,ScanLog log){

        log.setApps(params.getAllApps());
        log.setContent("mu ma !!!");
        log.setTime(createLogTime);
        log.setVirus(virusCnt);
        log.setUsedTime(params.getUsedTime());

        try {
            dbUtils.save(log);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPackageScaned(ScanProcess params, Object... other) {

        Log.i(TAG, "label is = : " + params.getLabel());

        /**
         * remember how many virus
         */
        if (params.isVirus()) {
            virusCnt++;
        }
        /**
         *  scan over and save to db
         */
        if (params.getAllApps() == params.getCurrentAppIndex()) {
            createLogTime = System.currentTimeMillis();

            saveLogMsg(params, log);
            virusCnt = 0;

        }
    }
}
