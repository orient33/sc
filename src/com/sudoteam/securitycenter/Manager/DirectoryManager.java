package com.sudoteam.securitycenter.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Created by huayang on 14-10-29.
 */
public class DirectoryManager {

    public static final String ROOT = "root";
    public static final String PATH = "path";

    private static Context context;

    private static DirectoryManager mInstance = new DirectoryManager();

    private DirectoryManager() {}

    public static DirectoryManager getInstance(Context ctx){

        context = ctx;
        return mInstance;
    }

    /**
     *
     * @param rootDirectory such as /mnt/sdcard
     * @return
     */
    public DirectoryManager setRootDirectory(String rootDirectory){

        File root = new File(rootDirectory);
        if(!root.exists()){
            root.mkdirs();
        }

        savePathToSp(ROOT,rootDirectory);

        return mInstance;
    }

    /**
     *
     * @param subDirectory such as music ,so the whole path is /mnt/sdcard/music..
     *                     perhaps subdirectory is music/lyric ,so the whole path is
     *                     /mnt/sdcard/music/lyric
     * @return
     */
    public DirectoryManager setSubDirectory(String subDirectory){

        String sub = getRootDirectory() + "/" + subDirectory ;
        File subFile = new File(sub);

        if(! subFile.exists()){
            subFile.mkdirs();
        }

        savePathToSp(subDirectory,sub);
        return mInstance;
    }



    public void savePathToSp(String index,String path){

        SharedPreferences sp = context.
                getSharedPreferences(PATH,Context.MODE_PRIVATE);

        sp.edit().putString(index,path).commit();

    }

    public String getRootDirectory(){

        return context.
                getSharedPreferences(PATH,Context.MODE_PRIVATE).getString(ROOT,"");
    }
}
