package com.sudoteam.securitycenter.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
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

import com.sudoteam.securitycenter.entity.ItemData;
import com.sudoteam.securitycenter.views.LineView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sudoteam.securitycenter.entity.ScanLog;
import com.sudoteam.securitycenter.entity.ScanProcess;
import com.sudoteam.securitycenter.manager.*;
import com.sudoteam.securitycenter.views.SingleClickButton;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.constant.ScanVirus;

import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;
import com.sudoteam.securitycenter.Util;

import org.w3c.dom.Text;


public class ScanVirusActivity extends SuperActivity implements
								View.OnClickListener ,ScanVirusManager.ScanPackageListener {

    public static String TAG = "MainActivity";
    
    private LineView itemContainer;
    private ScrollView scrollView;
    private Handler handler = new Handler();
    private RelativeLayout scanningLayout,scanOverHealth;

    private Button scanMask;
    private Button appIcon;
    private Button virusOperation;
    private ProgressBar progressBar;
    private TextView scaningAppName;
    private Animation zoomOut,zoomIn,ra;
    private Button scanLogList;
    private TextView scanOverUsedTime;

    private DbUtils dbUtils;
    private int scanCnt=0;
    private int virusCnt = 0;
    private int warnings = 0;

    private long createLogTime = 0;
    private ScanLog log ;

    private ScanVirusManager mScanVirusManager;
    private List<PackageInfo> mUserInfos;

    private int currentScanningState = ScanVirus.SCAN_STATE_UNCOMPLETE;
    private int buttonState = ScanVirus.SCAN_WORKING;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_virus);

        Util.setActionBar(this, true, "病毒扫描", R.drawable.scan_log_list,this);

        zoomOut = AnimationUtils.loadAnimation(this,R.anim.zoom_out);
        zoomIn = AnimationUtils.loadAnimation(this,R.anim.zoom_in);

        scanningLayout = (RelativeLayout) findViewById(R.id.scan_page_parent_scanning);
        scanningLayout.setOnClickListener(this);

        appIcon = (Button) findViewById(R.id.scan_app_icon);
        progressBar = (ProgressBar) findViewById(R.id.scan_progress);
        scaningAppName = (TextView) findViewById(R.id.current_scaning_package_name);
        scanOverHealth = (RelativeLayout) findViewById(R.id.scan_page_parent_over);

        virusOperation = (Button)findViewById(R.id.virus_operation);
        virusOperation.setOnClickListener(this);

        scanOverUsedTime = (TextView)findViewById(R.id.scan_over_used_time);

        /**
         * scanning animation
         */
        scanMask = (Button) findViewById(R.id.scan_mask);
        ra = AnimationUtils.loadAnimation(this,R.anim.scan_mask_rotate);
        ra.setInterpolator(new LinearInterpolator());
        scanMask.setAnimation(ra);
        ra.start();

        itemContainer = (LineView)findViewById(R.id.item_container);
        scrollView = (ScrollView)findViewById(R.id.checked_list);

        mScanVirusManager = ScanVirusManager.getInstance(this);
        mUserInfos = mScanVirusManager.getUserInstalledPackagesInfo();

        dbUtils = DbUtils.create(this,ScanVirus.SCAN_LOG_DB);

        /**
         * set max number of progressbar
         */
        progressBar.setMax(mUserInfos != null ? mUserInfos.size():0);


        scanPackages();

    }


    private void scanPackages(){

        /**
         * each scan action will create a piece of log in db
         */
        log = new ScanLog();

        mScanVirusManager
                .setOnPackageScanedListener(this)
                .scanPackages();

    }

    /**
     * create an item of scanning listview
     * @param isLast
     * @param name
     * @param icon
     */
    private void createListItem(boolean isLast,String name,Drawable icon){

        //itemContainer.setProblemView(ViewUtils.getView(getActivity(),R.layout.test_view));

        ItemData data = itemContainer.createAData(isLast);
        data.setTitle(name);
        data.setIcon(icon);
        itemContainer.addViewByAnimation(data);

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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.scan_page_parent_scanning :

                break;

            case R.id.actionbar_setting:

                startActivity(new Intent(this,ScanLogActivity.class));

                break;

            case R.id.virus_operation:

                if(currentScanningState == ScanVirus.SCAN_STATE_UNCOMPLETE){

                    if(buttonState == ScanVirus.SCAN_WORKING){

                        buttonState = ScanVirus.SCAN_PAUSE;

                        virusOperation.setText("starting");

                        mScanVirusManager.stopScan();


                    }else if(buttonState == ScanVirus.SCAN_PAUSE){

                        buttonState = ScanVirus.SCAN_WORKING;

                        virusOperation.setText("pause");
                        mScanVirusManager.startScan();
                    }
                }else {

                    if(buttonState == ScanVirus.SCAN_COMPLETE_AND_BACK){

                        //exit
                        finish();

                    }else if(buttonState == ScanVirus.SCAN_CLEAR_VIRUS){

                        //clear & exit set the label of button : "clearing the virus "
                        overScanAndClear();
                    }

                }

                break;
        }
    }

    /**
     * scan over and do something ...
     */
    private void overScanAndClear() {

        virusOperation.setText("now clearing ...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch(Exception e){

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        log.setClears(11);

                        try {
                            dbUtils.update(log, WhereBuilder.b().expr("time = " + createLogTime),"clears");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }
                });

            }
        }).start();

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
    public void onPackageScaned(final ScanProcess params, Object... other) {

        Log.i(TAG, "label is = : " + params.getLabel());

        /**
         * remember how many virus
         */
        if (params.isVirus()) {
            virusCnt++;
        }


        handler.post(new Runnable() {
            @Override
            public void run() {

                /**
                 * set current scanning app icon
                 */
                appIcon.setBackground(params.getIcon());
                appIcon.setAnimation(zoomOut);
                zoomOut.start();

                /**
                 * set current scanning app name
                 */
                scaningAppName.setText("正在扫描 : " + params.getLabel());
                progressBar.setProgress(++scanCnt);
                createListItem(scanCnt==mUserInfos.size(),params.getLabel(),params.getIcon());

                /**
                 * scan virus over OR clicked the pause button
                 */
                if(scanCnt == mUserInfos.size()) {

                    scanCnt = 0;

                    /**
                     * cancel the scanning animation
                     */
                    scanMask.clearAnimation();
                    ra.cancel();
                    scanMask.setVisibility(View.INVISIBLE);
                    scanningLayout.setVisibility(View.INVISIBLE);
                    scanningLayout.setAnimation(zoomIn);
                    zoomIn.start();

                    /**
                     * show health layout with animation
                     */
                    scanOverHealth.setVisibility(View.VISIBLE);
                    scanOverHealth.setAnimation(zoomOut);
                    zoomOut.start();

                    /**
                     * save log to db
                     */
                    createLogTime = System.currentTimeMillis();
                    saveLogMsg(params, log);
                    virusCnt = 0;
                    Log.i("Tag","scan over and save data to db !");

                    /**
                     * scan over state
                     */
                    currentScanningState = ScanVirus.SCAN_STATE_COMPLETE;

                    buttonState = ScanVirus.SCAN_CLEAR_VIRUS;

                    /**
                     * scan over and the button tell you how to do
                     */
                    if(buttonState == ScanVirus.SCAN_COMPLETE_AND_BACK){

                        //exit
                        virusOperation.setText("over");

                    }else if(buttonState == ScanVirus.SCAN_CLEAR_VIRUS){

                        //clear & exit set the label of button : "clearing the virus "

                        virusOperation.setText("clear");
                    }

                    /**
                     * how long from start to stop
                     */
                    scanOverUsedTime.setText("used time : " + params.getUsedTime());
                }
            }
        });

    }
}
