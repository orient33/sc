
package com.example.block;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.lang.Exception;
import java.lang.Override;
//import com.sudoteam.securitycenter.Manager.*;

public class WorkService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;//mBinder;
    }

/*    public ICheckSms.Stub mBinder = new ICheckSms.Stub(){

        @Override
        public boolean shouldBlockThisSmsByNumber(String number){


            if(MessageManager.getInstance(WorkService.this).isTrashMsgByNum(number)){

                try {
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.i("huayang","you are blocked here !");

                return true;
            }else {
                Log.i("huayang","dont block this number ... ");
                return false;
            }

        }

        @Override
        public boolean shouldBlockThisSmsByContent(String content){

            Log.i("huayang","shouldBlockThisSmsByContent");
            return true;
        }

    };
*/
}