package com.sudoteam.securitycenter.Manager;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by huayang on 14-10-28.
 */
public class NetworkManager {

    private String TAG = "NetworkManager";

    private static Context context;

    private static NetworkManager mInstance = new NetworkManager();

    private HttpUtils httpUtils;

    private NetworkManager() {

        httpUtils = new HttpUtils();
    }

    public static NetworkManager getInstance(Context ctx){

        context = ctx;
        return mInstance;
    }

    public interface DownloadListener {
        public void onDownloading(int percent);

        public void onSuccess();

        public void onFailed();

        public void onStart();
    }

    public void download(String url, final String path, final DownloadListener listener){

        httpUtils.download(url,path,
                true,true,new RequestCallBack<File>() {

            @Override
            public void onStart() {
                Log.i(TAG,"start ... ");

                if(listener != null)
                    listener.onStart();

            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {

                Log.i(TAG,"loading ... " + current*100/total + " %");

                if(listener != null) {
                    if(total != 0)
                        listener.onDownloading((int)(current*100/total));
                }
            }

            @Override
            public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                Log.i(TAG,"stop ... ");
                if(listener != null)
                    listener.onSuccess();
            }

            @Override
            public void onFailure(HttpException e, String s) {

                /**
                 * if download failed ,delete self
                 */
                File tmpFile = new File(path);
                if(tmpFile.exists()){
                    tmpFile.delete();
                }

                if(listener != null)
                    listener.onFailed();

            }
        });
    }

}
