package com.sudoteam.securitycenter.Activity;

import android.app.Application;
import android.os.Environment;

import com.sudoteam.securitycenter.Manager.DirectoryManager;

/**
 * Created by huayang on 14-10-29.
 */
public class SecurityApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String root = Environment.getExternalStorageDirectory() + "/" + "SSS";

        DirectoryManager.getInstance(this).setRootDirectory(root)
                .setSubDirectory("trash")
                .setSubDirectory("network")
                .setSubDirectory("block")
                .setSubDirectory("battary")
                .setSubDirectory("virus")
                .setSubDirectory("permission")
                .setSubDirectory("permission/all")
                .setSubDirectory("test/aa");

    }
}
