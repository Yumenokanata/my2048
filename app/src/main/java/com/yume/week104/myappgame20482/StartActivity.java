package com.yume.week104.myappgame20482;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.yume.week104.myappgame20482.database.SqliteHelper;


public class StartActivity extends Activity implements Constants {
    SqliteHelper mSqliteHelper;
    SQLiteDatabase mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mSqliteHelper = new SqliteHelper(StartActivity.this);
        mDataBase = mSqliteHelper.getWritableDatabase();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SqliteHelper.RestoreData restoreData = SqliteHelper.getRestoreData(mDataBase);
                if(restoreData.data != null){
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    intent.putExtra(INTENT_MODE, restoreData.mode);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
                }else{
                    startActivity(new Intent(StartActivity.this, MenuActivity.class));
                    finish();
                    overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
