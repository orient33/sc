package com.sudoteam.securitycenter.receiver;

import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.System;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.android.internal.telephony.ITelephony;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {

    String TAG = "PhoneState";
    private TelephonyManager telephonyManager;

    private static long startTime,endTime;

    @Override
    public void onReceive(Context context, Intent intent) {

        telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

        switch (telephonyManager.getCallState()) {

            case TelephonyManager.CALL_STATE_RINGING:

                startTime = System.currentTimeMillis();

                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.i(TAG, "number:" + number);

                if ("5554".equals(number)) {

                    Log.i(TAG, "will end call");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {

                            }

                            endCall();

                        }
                    }).start();

                }
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:

                break;

            case TelephonyManager.CALL_STATE_IDLE:

                endTime = System.currentTimeMillis();

                if(endTime > startTime && (endTime - startTime) < 2000){

                    startTime = endTime = 0;

                    /**
                     * ring less than 2s
                     */

                    //do something
                }

                break;
        }

    }


    private void endCall() {

        Class<TelephonyManager> c = TelephonyManager.class;

        try {

            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager, (Object[]) null);
            iTelephony.endCall();

        } catch (Exception e) {
            Log.i(TAG, "Fail to answer ring call.", e);
        }
    }


}