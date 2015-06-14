package com.yume.week104.myappgame20482.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yume.week104.myappgame20482.Constants;

/**
 * Created by yume on 2015/6/13.
 */
public class SqliteHelper extends SQLiteOpenHelper implements Constants {
    private static final int VERSION = 3;

    private static final String TB_FILE_NAME = "database.db";

    private static final String TB_HISTORY = "history_score";
    private static final String HISTORY_C_MODE = "mode";
    private static final String HISTORY_C_MAX_SCORE = "max_score";
    private static final String HISTORY_C_INDEX_W = "index_w";
    private static final String HISTORY_C_INDEX_H = "index_h";

    private static final String TB_RESTORE_DATA = "restore_data";
    private static final String RESTORE_C_MODE = "mode";
    private static final String RESTORE_C_DATA = "data";
    private static final String RESTORE_C_SCORE = "score";

    public SqliteHelper(Context context){
        this(context, TB_FILE_NAME, null, VERSION);
    }

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (_id integer primary key, %s integer, %s text, %s integer, %s integer)",
                TB_HISTORY,
                HISTORY_C_MODE,
                HISTORY_C_MAX_SCORE,
                HISTORY_C_INDEX_W,
                HISTORY_C_INDEX_H));
        db.execSQL(String.format("create table %s (_id integer primary key, %s integer, %s text, %s text)", TB_RESTORE_DATA, RESTORE_C_MODE, RESTORE_C_DATA, RESTORE_C_SCORE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static String getMaxScore(SQLiteDatabase db, int mode, int indexW, int indexH){
        Cursor cursor = db.rawQuery(String.format("select %s from %s where %s = ? and %s = ? and %s = ?", HISTORY_C_MAX_SCORE, TB_HISTORY, HISTORY_C_MODE, HISTORY_C_INDEX_W, HISTORY_C_INDEX_H),
                new String[]{mode + "", indexW + "", indexH + ""});

        String score = "0";
        while (cursor.moveToNext()){
            score = cursor.getString(0);
        }
        cursor.close();
        return score;
    }

    public static void putMaxScore(SQLiteDatabase db, int mode, int indexW, int indexH, String score){
        db.execSQL(String.format("delete from %s where %s = ? and %s = ? and %s = ?", TB_HISTORY, HISTORY_C_MODE, HISTORY_C_INDEX_W, HISTORY_C_INDEX_H),
                new String[]{mode + "", indexW + "", indexH + ""});
        db.execSQL(String.format("insert into %s values(null, ?, ?, ?, ?)", TB_HISTORY), new String[]{mode + "", score, indexW + "", indexH + ""});
    }

    public static class RestoreData{
        public int mode;
        public String data;
        public String score;
        public RestoreData(int mode, String data, String score){
            this.mode = mode;
            this.data = data;
            this.score = score;
        }
    }

    public static RestoreData getRestoreData(SQLiteDatabase db){
        Cursor cursor = db.rawQuery(String.format("select %s, %s, %s from %s", RESTORE_C_DATA, RESTORE_C_SCORE, RESTORE_C_MODE, TB_RESTORE_DATA), new String[]{});

        String data = null;
        String score = "0";
        int mode = MODE_NORMAL;
        while (cursor.moveToNext()){
            data = cursor.getString(0);
            score = cursor.getString(1);
            mode = cursor.getInt(2);
        }
        cursor.close();
        return new RestoreData(mode, data, score);
    }

    public static void putRestoreData(SQLiteDatabase db, RestoreData restoreData){
//        db.execSQL("delete from " + TB_RESTORE_DATA + " where " + RESTORE_C_MODE + " = " + restoreData.mode);
        db.execSQL("delete from " + TB_RESTORE_DATA);
        db.execSQL(String.format("insert into %s values(null, ?, ?, ?)", TB_RESTORE_DATA), new String[]{restoreData.mode + "", restoreData.data, restoreData.score + ""});
    }

    public static void clearRestoreData(SQLiteDatabase db){
        db.execSQL("delete from " + TB_RESTORE_DATA);
    }
}
