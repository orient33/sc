package com.sudoteam.securitycenter.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.content.Intent;
import android.util.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.sudoteam.securitycenter.entity.*;
import com.lidroid.xutils.db.sqlite.Selector;

import java.lang.Deprecated;
import java.util.List;

/**
 * Created by huayang on 14-10-24.
 */
public class MessageManager {

    private static MessageManager mInstance = new MessageManager();

    private static Context context;

    private MessageManager() {
    }

    public static MessageManager getInstance(Context ctx){
        context = ctx;
        return mInstance;
    }

    public void insertMessageToSystemDatabase(){

        try{
            ContentValues values = new ContentValues();
            // 发送时间
            values.put("date", System.currentTimeMillis());
            // 阅读状态
            values.put("read", 0);
            // 类型：1为收，2为发
            values.put("type", 1);
            // 发送号码
            values.put("address","12345678");
            // 发送内容
            values.put("body", "this is my wife , i love her !");
            // 插入短信库
            Uri uri = context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
//            context.getContentResolver().notifyChange(Uri.parse("content://sms"),null);
//            Intent intent = new Intent(ACTION_NOTIFY_DATASET_CHANGED);
//            context.sendBroadcast(intent);

            Log.i("huayang","insert over ... " + uri.toString());

        }catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }

    }

    public void insertMessageToUserDatabase(DbUtils dbUtils,Intent intent){


        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        if(msgs.length > 0 && msgs.length == 1){

            Log.i("huayang","only one sms ... ");
            SmsMessage message = msgs[0];
            if(message != null){

                saveToDatabase(dbUtils,message.getDisplayMessageBody(),
                        message.getOriginatingAddress(),
                        String.valueOf(message.getTimestampMillis()));

                Log.i("huayang","message is :" + message.getDisplayMessageBody());
            }
        }else {

            Log.i("huayang","more than one messages");

            StringBuilder sb = new StringBuilder();
            SmsMessage tmpSmsMessage = null;
            for(SmsMessage msg : msgs){

                tmpSmsMessage = msg;
                sb.append(msg.getDisplayMessageBody());
            }

            Log.i("huayang","message is :" + sb.toString());

            saveToDatabase(dbUtils,sb.toString(),
                    tmpSmsMessage.getOriginatingAddress(),
                    String.valueOf(tmpSmsMessage.getTimestampMillis()));

        }

    }

    public void saveToDatabase(DbUtils dbUtils,String content,String addrFrom,String recvTime,String ... other){

        Log.i("huayang","save data to database");
        Log.i("huayang","save data to database" + content);
        Log.i("huayang","save data to database" + addrFrom);
        Log.i("huayang","save data to database" + recvTime);

        TrashSms trashSms = new TrashSms();
        trashSms.setContent(content);
        trashSms.setAddrFrom(addrFrom);
        trashSms.setRecvTime(recvTime);


        try {
            dbUtils.save(trashSms);
            Log.i("huayang","trash sms saved over ~~~~~~~");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //test(dbUtils);
    }


    public List<TrashSms> getTrashSms(DbUtils dbUtils){

        List<TrashSms> trashSmses = null;
        try {
            trashSmses = dbUtils.findAll(Selector.from(TrashSms.class)
                    .orderBy("id"));

        } catch (DbException e) {
            e.printStackTrace();
        }

        return trashSmses;
    }

    @Deprecated
    public void test(DbUtils dbUtils){

        for(TrashSms s : getTrashSms(dbUtils)){

            Log.i("huayang", "the content is :" + s.getContent());
        }
    }

    /**
     * if the msg is trash by bumber
     * @param number
     * @return
     */
    public boolean isTrashMsgByNum(String number){

        DbUtils dbUtils = DbUtils.create(context,"/mnt/sdcard","telephony.db");

        List<BlockNumbers> numbs = null;

        try {
            numbs = dbUtils.findAll(Selector.from(BlockNumbers.class)
                    .where("number","=",number)
                    .orderBy("id"));

        } catch (DbException e) {
            e.printStackTrace();
        }

        if(numbs != null && numbs.size() > 0){

            Log.i("huayang","the number is : " + numbs.get(0).getNumber() + "  the name is" + numbs.get(0).getName());

            return true;
        }

        return false;
    }

    /**
     * for test ...
     */
    public void createDb(){

        DbUtils dbUtils = DbUtils.create(context,"/mnt/sdcard","telephony.db");

        BlockNumbers bn = new BlockNumbers();
        bn.setNumber("10010");
        bn.setName("China Unicom");

        try {
            dbUtils.save(bn);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
