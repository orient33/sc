package com.sudoteam.securitycenter.netstat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.RemoteException;

import java.util.HashMap;

/**
 * 每天的流量使用数据 如 1号使用了10MB  2号使用了15MB(实际使用15-10)
 */
public class DataUsageDatabase extends SQLiteOpenHelper {
    private static final String FILENAME = "data-usage.db";
    private static final String TABLE = "data";
    private static final String CLUMNS[] = new String[]{"_id", "date", "used"};
    private static DataUsageDatabase ins;

    private DataUsageDatabase(Context context, String name, SQLiteDatabase.CursorFactory cf, int version) {
        super(context, name, cf, version);
    }

    public static DataUsageDatabase getIns(Context c) {
        if (ins == null)
            ins = new DataUsageDatabase(c, FILENAME, null, 1);
        return ins;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE + " ( " +
                CLUMNS[0] + " integer primary key autoincrement , " +
                CLUMNS[1] + " text, " +  // 2011-11-11
                CLUMNS[2] + " long " +  // used size.
                " );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void insert(String date, long used) {
        final SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues(3);
        cv.put(CLUMNS[1], date);
        cv.put(CLUMNS[2], used);
        db.insert(TABLE, null, cv);
        db.close();
    }
    private long query(String date){
        long result = -1;
        final SQLiteDatabase db = getReadableDatabase();
        String sel = CLUMNS[1] +" = '"+date+"'";
        Cursor cursor = db.query(TABLE,new String[]{CLUMNS[2]},sel,null,null,null,null);
        if(cursor.moveToFirst()){
            result = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    private HashMap<String, Long> mCached = new HashMap<String, Long>();
    private INetworkStatsSession mINetworkStatsSession;
    public void setNS(INetworkStatsSession inss){
        mINetworkStatsSession = inss;
    }
    public long getUsedForDate(NetworkTemplate nt,String date,boolean needCache) {
        Long v = mCached.get(date); //先从cache获取
        if (v != null)
            return v;

        long result;
        result = query(date);   //再从DB中获取
        if(result >= 0){
            mCached.put(date,result);
            return result;
        }
        result = getNS(mINetworkStatsSession, nt, date);
        if(result >= 0 && needCache) {   //当天的是不需要缓存的 因为时间还不够一整天
            insert(date, result);
            mCached.put(date,result);
        }
        return result;
    }

    private static long getNS(INetworkStatsSession ns, NetworkTemplate nt,String date){
        long start =TimeUtils.getLongForDate(date);//获取start
        long end = start + TimeUtils.ONE_DAY;
        try {
            NetworkStats stats =  ns.getSummaryForNetwork(nt, start, end);
            return stats.getTotalBytes();
        }catch (RemoteException e){
            return -11;
        }
    }

}
