package com.sudoteam.securitycenter.optimizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class OptDatabase extends SQLiteOpenHelper {

    private static final String FILENAME = "optimizer.db";

    private static final String TASK_WHITE_LIST = "task_white_list";
    private static final String SDCLEAN_WHITE_LIST = "sdclean_white_list";
    private static OptDatabase sIns;

    private OptDatabase(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    public static OptDatabase get(Context c) {
        if (null == sIns)
            sIns = new OptDatabase(c, FILENAME, null, 1);
        return sIns;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table if not exists " + TASK_WHITE_LIST + " ( "
                + Optimize.TASK[0] + " integer primary key autoincrement , "
                + Optimize.TASK[1] + " integer , "
                + Optimize.TASK[2] + " text " + " );";

        db.execSQL(sql);

        sql = "create table if not exists " + SDCLEAN_WHITE_LIST + " ( "
                + Optimize.SDCLEAN[0] + " integer primary key autoincrement, "
                + Optimize.SDCLEAN[1] + " integer, "
                + Optimize.SDCLEAN[2] + " text, "
                + Optimize.SDCLEAN[3] + " integer " + " ); ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // :TODO
    }

    public List<String> getTaskList() {
        List<String> data = new ArrayList<String>();
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db
                .query(TASK_WHITE_LIST, new String[]{Optimize.TASK[2]},
                        null, null, null, null, null);
        // cursor never null?
        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                data.add(cur.getString(0));
                cur.moveToNext();
            }
        }
        cur.close();
        db.close();
        return data;
    }

    public void addTaskWhiteList(String taskName) { // :TODO sync lock?
        final SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues(3);
        values.put(Optimize.TASK[1], 1); // type ?
        values.put(Optimize.TASK[2], taskName);
        db.insert(TASK_WHITE_LIST, null, values);
        db.close();
    }

    public boolean removeTaskWhiteList(String taskName) {
        final SQLiteDatabase db = getWritableDatabase();
        int count;
        String where = Optimize.TASK[2] + " = '" + taskName + "'";
        count = db.delete(TASK_WHITE_LIST, where, null);
        db.close();
        return count > 0;
    }

    static class Optimize {
        static final String TASK[] = new String[]{"_id", // auto ++
                "type", // int 1
                "name", // text 2
        };
        static final String SDCLEAN[] = new String[]{"_id", // auto ++
                "type", // int 1
                "path", // text 2
                "size", // int 3
        };
    }
}
