package com.sudoteam.securitycenter.Manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sudoteam.securitycenter.Entity.ScanProcess;

/**
 * Created by huayang on 14-10-28.
 */
public class ScanVirusManager {

    private static final String TAG = "ScanVirusManager";

    public static final int FLAG_USER_INSTALLED_SIGNATURE = 1;
    public static final int FLAG_SYSTEM_SIGNATURE = 2;
    public static final int FLAG_ALL_SIGNATURE = 4;

    private static Context context;
    private static ScanVirusManager mInstance = new ScanVirusManager();
    private ScanPackageListener listener;
    private static PackageManager packageManager;


    public interface ScanPackageListener {

        /**
         * @param params
         * @param other
         */

        public void onPackageScaned(ScanProcess params,Object ... other);
    }

    public ScanVirusManager setOnPackageScanedListener(ScanPackageListener listener){

        this.listener = listener;

        return mInstance;
    }

    private ScanVirusManager() {

    }

    public static ScanVirusManager getInstance(Context ctx){

        context = ctx;
        if(packageManager == null)
            packageManager = context.getPackageManager();

        return  mInstance;
    }

    /**
     *
     */
    public ScanVirusManager scanPackages(){

        new ScanTask().execute();

        return mInstance;
    }

    public class ScanTask extends AsyncTask<Void,Object,Void>{

        int allPkgs = getInstalledPkgNumber();
        private long startScan ;
        private long stopScan ;
        @Override
        protected Void doInBackground(Void... params) {


            List<PackageInfo> infos = getInstalledPackagesInfo();
            boolean virus = false;

            startScan = System.currentTimeMillis();

            for(int i=1; i<=allPkgs; i++){

                virus = isVirus();
                /***just for test **/
                if(i==2 || i==4 || i==6 || i==9)
                    virus = true;
                /******/
                publishProgress(infos.get(i-1),new Integer(i),new Boolean(virus));

            }

            stopScan = System.currentTimeMillis();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Object... values) {

            PackageInfo info = (PackageInfo) values[0];
            Integer i = (Integer) values[1];
            Boolean virus = (Boolean) values[2];

            if(listener != null){

                String name = (String) info.applicationInfo.loadLabel(packageManager);

                ScanProcess params = new ScanProcess();
                params.setLabel(name);
                params.setCurrentAppIndex(i);
                params.setAllApps(allPkgs);
                params.setVirus(virus);

                if(i == allPkgs) {
                    params.setUsedTime(getUsedTime(stopScan - startScan));
                    startScan = stopScan = 0;
                }
                params.setIcon(info.applicationInfo.loadIcon(packageManager));

                listener.onPackageScaned(params);

                Log.i(TAG,"current is = " + i + "  name is " + name + "  percent is : " + i*100/allPkgs + "%" + " virus =" + virus);
            }

        }
    }

    /**
     * @return
     */
    public boolean isVirus(){

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updateVirusDB(NetworkManager.DownloadListener listener){

        NetworkManager.
                getInstance(context).download("http://qianqian.baidu.com/download/ttpsetup-56013088.exe",
                "/mnt/sdcard/a.exe",listener);
    }


    public String getUsedTime(long time){

        Log.i(TAG,"time is " + time);
//        long days = time / (1000 * 60 * 60 * 24);
//        long hours = (time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (time % (1000 * 60)) / 1000;

        return minutes > 0 ? minutes + " 分 "+ seconds + " 秒" : seconds + " 秒";

    }

    /**
     * how many pkgs in number
     *
     * @return
     */
    public int getInstalledPkgNumber(){

        return  getInstalledPackagesInfo().size();
    }

    /**
     * get all installed packages in OS
     * @return
     */
    public List<PackageInfo> getInstalledPackagesInfo(){

        /**
         * getInstalledPackages(0)
         * another params will lead to PackageManager died !
         * such as PackageManager.GET_ACTIVITY
         */
        return  packageManager
                .getInstalledPackages(0);
    }

    /**
     * get system packages in OS
     * @return
     */
    public List<PackageInfo> getSystemPackagesInfo(){

        List<PackageInfo> pis = new ArrayList<PackageInfo>();
        for(PackageInfo info : getInstalledPackagesInfo()){

            if(!((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)){
                pis.add(info);
            }
        }

        return pis;
    }

    /**
     * get user installed packages in OS
     * @return
     */
    public List<PackageInfo> getUserInstalledPackagesInfo(){

        List<PackageInfo> pis = new ArrayList<PackageInfo>();

        for(PackageInfo info : getInstalledPackagesInfo()){

            Log.i("huayang", "the name is : " + (String) info.applicationInfo.loadLabel(packageManager));

            if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                pis.add(info);
            }
        }

        return pis;
    }

    /**
     * key = package name
     * val = package signature
     * @param flag what kind of package ?
     * @return
     */
    public Map<String,String> getPackageSignature(int flag){

        if((flag & FLAG_USER_INSTALLED_SIGNATURE) == 1){

            return getSignature(getUserInstalledPackagesInfo());
        }else if((flag & FLAG_SYSTEM_SIGNATURE) == 2){

            return getSignature(getSystemPackagesInfo());
        }else if((flag & FLAG_ALL_SIGNATURE) == 4){

            return getSignature(getInstalledPackagesInfo());
        }

        return null;
    }

    /**
     *
     * @param infos
     * @return
     */
    public Map<String,String> getSignature(List<PackageInfo> infos){

        Map<String,String> signatures = new HashMap<String, String>();
        String tmpSig = "";

        for(PackageInfo info : infos){

            if(info.signatures != null) {
                for (Signature sig : info.signatures) {

                    System.out.println("package name = " + info.packageName + "  size = " + info.signatures.length + "  signature = " + sig.toCharsString());
                    tmpSig += sig.toString();
                }

                signatures.put(info.packageName, tmpSig);
                tmpSig = "";
            }
        }

        return signatures;
    }
}
