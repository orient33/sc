package com.sudoteam.securitycenter.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import com.sudoteam.securitycenter.Entity.ScanLog;
import com.sudoteam.securitycenter.Entity.ScanProcess;
import com.sudoteam.securitycenter.Manager.NetworkManager;
import com.sudoteam.securitycenter.Manager.ScanVirusManager;

import com.sudoteam.securitycenter.Views.SingleClickButton;
import com.sudoteam.securitycenter.R;

public class ScanVirusActivity extends SuperActivity implements
        View.OnClickListener ,ScanVirusManager.ScanPackageListener{

    public static String TAG = "MainActivity";
    public static String DB = "security.db";

    @ViewInject(R.id.scan_virus_score)
    private TextView tv ;

    private SingleClickButton btn ;

    @ViewInject(R.id.app_icon)
    private ImageView icon;

    @ViewInject(R.id.update)
    private SingleClickButton update;

    @ViewInject(R.id.app_name)
    private TextView appName;

    @ViewInject(R.id.used_time)
    private TextView usedTime;

    @ViewInject(R.id.scan_log)
    private Button scanLog;

    private DbUtils dbUtils ;

    private int virusCnt = 0;
    private int warnings = 0;

    private long createLogTime = 0;
    private ScanLog log ;

    private Button scanOver;

    @OnClick(R.id.update)
    public void onUpdateClick(View view){
        Log.i(TAG,"onclicked ... ");

        ScanVirusManager.getInstance(this).updateVirusDB(new NetworkManager.DownloadListener() {

            @Override
            public void onStart() {
                update.setActive(false);
            }

            @Override
            public void onDownloading(int percent) {
                bar.setProgress(percent);
            }

            @Override
            public void onSuccess() {
                update.setActive(true);
            }

            @Override
            public void onFailed() {
                update.setActive(true);
            }
        });

    }

    @OnClick(R.id.btn)
    public void onScanClick(View view){

    }

    @ViewInject(R.id.download)
    ProgressBar bar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        ViewUtils.inject(this);

        dbUtils = DbUtils.create(this,DB);

        btn = (SingleClickButton) findViewById(R.id.btn);
        btn.setOnClickListener(this);

        scanOver = (Button) findViewById(R.id.scan_over);
        scanOver.setOnClickListener(this);
    }

    @OnClick(R.id.scan_log)
    public void showScanLog(View view){

        startActivity(new Intent(this,ScanLogActivity.class));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn :

                /**
                 * if the button can trigger an action
                 */
                if(((SingleClickButton)v).isActive()) {
                    startScan();

                    /**
                     * if sure ,let the button NOT actived,until the task over
                     */
                    ((SingleClickButton) v).setActive(false);
                }
                break;

            case R.id.scan_over :
                overScanAndClear();
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
         * show scan info
         */
        icon.setImageDrawable(params.getIcon());
        tv.setText(params.getCurrentAppIndex() * 100 / params.getAllApps() + " %" );
        appName.setText(params.getLabel());

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

            usedTime.setText("耗时：" + params.getUsedTime());
            createLogTime = System.currentTimeMillis();

            saveLogMsg(params, log);
            virusCnt = 0;

            /**
             * let the button actived
             */
            btn.setActive(true);
        }
    }
}
