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
 * size of data usage every day.
 */
public class DataUsageDatabase extends SQLiteOpenHelper {
    private static final String FILENAME = "data-usage.db";
    private static final String TABLE = "data";
    private static final String COLUMNS[] = new String[]{"_id", "date", "used"};
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
                COLUMNS[0] + " integer primary key autoincrement , " +
                COLUMNS[1] + " text, " +  // 2011-11-11
                COLUMNS[2] + " long " +  // used size.
                " );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void insert(String date, long used) {
        final SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues(3);
        cv.put(COLUMNS[1], date);
        cv.put(COLUMNS[2], used);
        db.insert(TABLE, null, cv);
        db.close();
    }

    private long query(String date) {
        long result = -1;
        final SQLiteDatabase db = getReadableDatabase();
        String sel = COLUMNS[1] + " = '" + date + "'";
        Cursor cursor = db.query(TABLE, new String[]{COLUMNS[2]}, sel, null, null, null, null);
        if (cursor.moveToFirst()) {
            result = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    private HashMap<String, Long> mCached = new HashMap<String, Long>();
    private INetworkStatsSession mINetworkStatsSession;

    public void setNS(INetworkStatsSession inss) {
        mINetworkStatsSession = inss;
    }

    public long getUsedForDate(NetworkTemplate nt, String date, boolean needCache) {
        Long v = mCached.get(date); //1 get from cache
        if (v != null)
            return v;

        long result;
        result = query(date);   //2 get from DB if failed from cache
        if (result >= 0) {
            mCached.put(date, result);
            return result;
        }
        long start = NetUtils.getLongForDate(date);// get start time
        long end = start + NetUtils.ONE_DAY;
        result = NetUtils.getNS(mINetworkStatsSession, nt, start,end);// 3 query. if failed form cache & DB
        if (result >= 0 && needCache) {   // cache result to DB & cache, except today
            insert(date, result);
            mCached.put(date, result);
        }
        return result;
    }


}
