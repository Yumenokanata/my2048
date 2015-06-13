package com.yume.week104.myappgame20482.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yume on 2015/6/13.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;

    private static final String TB_FILE_NAME = "database.db";

    private static final String TB_HISTORY = "history_score";
    private static final String C_MAX_SCORE = "max_score";

    private static final String TB_RESTORE_DATA = "restore_data";
    private static final String C_DATA = "data";
    private static final String C_SCORE = "score";

    public SqliteHelper(Context context){
        this(context, TB_FILE_NAME, null, VERSION);
    }

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (_id integer primary key, %s integer)", TB_HISTORY, C_MAX_SCORE));
        db.execSQL(String.format("create table %s (_id integer primary key, %s text, %s integer)", TB_RESTORE_DATA, C_DATA, C_SCORE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static int getMaxScore(SQLiteDatabase db){
        Cursor cursor = db.rawQuery(String.format("select %s from %s", C_MAX_SCORE, TB_HISTORY), new String[]{});

        int score = 0;
        while (cursor.moveToNext()){
            score = cursor.getInt(0);
        }
        return score;
    }

    public static void putMaxScore(SQLiteDatabase db, int score){
        db.execSQL("delete from " + TB_HISTORY);
        db.execSQL(String.format("insert into %s values(null, ?)", TB_HISTORY), new String[]{score + ""});
    }

    public static class RestoreData{
        public String data;
        public int score;
        public RestoreData(String data, int score){
            this.data = data;
            this.score = score;
        }
    }

    public static RestoreData getRestoreData(SQLiteDatabase db){
        Cursor cursor = db.rawQuery(String.format("select %s, %s from %s", C_DATA, C_SCORE, TB_RESTORE_DATA), new String[]{});

        String data = null;
        int score = 0;
        while (cursor.moveToNext()){
            data = cursor.getString(0);
            score = cursor.getInt(1);
        }
        return new RestoreData(data, score);
    }

    public static void putRestoreData(SQLiteDatabase db, RestoreData restoreData){
        db.execSQL("delete from " + TB_RESTORE_DATA);
        db.execSQL(String.format("insert into %s values(null, ?, ?)", TB_RESTORE_DATA), new String[]{restoreData.data, restoreData.score + ""});
    }
}
