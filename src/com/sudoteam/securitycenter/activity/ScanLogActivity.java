package com.sudoteam.securitycenter.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.sudoteam.securitycenter.Adapter.*;
import com.sudoteam.securitycenter.Entity.ScanLog;
import com.sudoteam.securitycenter.Entity.ScanLogResult;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.Views.*;
import com.sudoteam.securitycenter.Activity.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.Window;


public class ScanLogActivity extends SuperActivity {

    private static final String TAG = "ScanLogActivity";
    private ListView logs;

    private DbUtils dbUtils ;

    ScanLogAdapter adapter ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_log);

        Util.setActionBar(this, true, "扫描日志", 0,null);

        logs = (ListView) findViewById(R.id.scan_log_list);
        adapter = new ScanLogAdapter(this,getScanLogResult());
        logs.setAdapter(adapter);


    }

    /**
     * make some infomations that will be showed in UI
     * @return
     */
    public List<ScanLogResult> getScanLogResult(){

        List<ScanLogResult> results = new ArrayList<ScanLogResult>();

        if(getScanLog() == null) {
            Log.i(TAG, "no data");
            return null;
        }

        for (ScanLog log : getScanLog()){

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(log.getTime());
            String result = "扫描个数 " + log.getApps() + "; 病毒 " + log.getVirus() + ";  清除 " + log.getClears() + "; 警告 " + log.getWarnings() ;

            Log.i(TAG,result);
            ScanLogResult r = new ScanLogResult();
            r.setTime(time);
            r.setResult(result);

            results.add(r);
        }

        return results;

    }

    /**
     * get all scan-logs from db
     * @return
     */
    private List<ScanLog> getScanLog(){

        List<ScanLog> logs = new ArrayList<ScanLog>();
        try {

            dbUtils = DbUtils.create(this, ScanVirusActivity.DB);
            logs = dbUtils.findAll(Selector.from(ScanLog.class).orderBy("id"));
        } catch (DbException e) {
            e.printStackTrace();
        }

        return logs;
    }
}
