package com.yume.week104.myappgame20482;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yume.week104.myappgame20482.database.SqliteHelper;
import com.yume.week104.myappgame20482.twozerogame.TwoGameView;


public class MainActivity extends Activity implements Constants, GameView.OnGameStatusChangedListener {
    private static final String TAG = "MainActivity";
    public static final int RESULT_RELOAD = 0x11;
    public static final int RESULT_BACK = 0x22;

    TextView nowScore_TextView, historyScore_TextView, title_mode_TextView;
    Button menu_Button, restart_Button;
    TwoGameView twoGameView;

    SqliteHelper mSqliteHelper;
    SQLiteDatabase mDataBase;

    int mMode;

    int mMaxScore = 0;
    int mNowScore = 0;

    boolean isRightFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title_mode_TextView = (TextView) findViewById(R.id.title_mode_TextView);
        twoGameView = (TwoGameView) findViewById(R.id.twoGameView);
        nowScore_TextView = (TextView) findViewById(R.id.nowScore_TextView);
        historyScore_TextView = (TextView) findViewById(R.id.historyScore_TextView);
        menu_Button = (Button) findViewById(R.id.menu_Button);
        restart_Button = (Button) findViewById(R.id.restart_Button);

        mSqliteHelper = new SqliteHelper(MainActivity.this);
        mDataBase = mSqliteHelper.getWritableDatabase();

        Intent intent = getIntent();
        mMode = intent.getIntExtra(INTENT_MODE, MODE_NORMAL);
        int index_w = intent.getIntExtra(INTENT_INDEX_W, -1);
        int index_h = intent.getIntExtra(INTENT_INDEX_H, -1);
        Log.d(TAG, "index_w = " + index_w + "index_h = " + index_h);
        if(index_w != -1 && index_h != -1){
            TwoGameView.INDEX_W = index_w;
            TwoGameView.INDEX_H = index_h;
        }

        switch (mMode){
            case MODE_OBSTACLE:
                title_mode_TextView.setText("障碍模式");
                break;
            case MODE_CRAZY:
                title_mode_TextView.setText("疯狂模式");
                break;
            default:
                title_mode_TextView.setText("经典模式");
                break;
        }

        nowScore_TextView.setText("0");
        menu_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("确定返回主菜单?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SqliteHelper.clearRestoreData(mDataBase);
                                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            }
        });
        restart_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twoGameView.reset();
                twoGameView.setOnGameStatusChangedListener(MainActivity.this);
                mNowScore = 0;
                nowScore_TextView.setText(mNowScore + "");
            }
        });

        twoGameView.setMode(mMode);
        twoGameView.setSeed(GameView.randomProductionSeed());
        SqliteHelper.RestoreData restoreData = SqliteHelper.getRestoreData(mDataBase);
        if(restoreData.data != null && restoreData.mode == mMode){
            twoGameView.setData(restoreData.data);
            nowScore_TextView.setText(restoreData.score + "");
            mNowScore = Integer.valueOf(restoreData.score);
        }else{
            SqliteHelper.clearRestoreData(mDataBase);
        }

        mMaxScore = Integer.valueOf(SqliteHelper.getMaxScore(mDataBase, mMode, twoGameView.getIndexW(), twoGameView.getIndexH()));
        historyScore_TextView.setText(mMaxScore + "");

        twoGameView.setOnGameStatusChangedListener(this);
        twoGameView.startUpdateTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_RELOAD){
            twoGameView.reset();
            twoGameView.setOnGameStatusChangedListener(this);
            mNowScore = 0;
            nowScore_TextView.setText(mNowScore + "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        SqliteHelper.putMaxScore(mDataBase, mMaxScore);
//        String restoreData = twoGameView.getDataString();
//        Log.d(TAG, "onPause restoreData: " + restoreData);
//        SqliteHelper.putRestoreData(mDataBase, restoreData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SqliteHelper.putMaxScore(mDataBase, mMode, twoGameView.getIndexW(), twoGameView.getIndexH(), mMaxScore + "");
        if(mMode != MODE_CRAZY && !isRightFinish){
            String restoreData = twoGameView.getDataString();
//        Log.d(TAG, "onStop restoreData: " + restoreData);
            SqliteHelper.putRestoreData(mDataBase, new SqliteHelper.RestoreData(mMode, restoreData, mNowScore + ""));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // 游戏监听方法
    @Override
    public void startGame() {

    }

    @Override
    public void checkFail() {

    }

    @Override
    public void checkSuccess() {

    }

    @Override
    public void checkSuccess(int score) {
        mNowScore += score;
        if(mMaxScore < mNowScore)
            mMaxScore = mNowScore;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nowScore_TextView.setText(mNowScore + "");
                historyScore_TextView.setText(mMaxScore + "");
            }
        });
    }

    @Override
    public void completeOnRound(boolean have2048) {
        SqliteHelper.putMaxScore(mDataBase, mMode, twoGameView.getIndexW(), twoGameView.getIndexH(), mMaxScore + "");

        Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
        intent.putExtra(INTENT_SCORE, mNowScore);
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
        Log.d(TAG, "game over");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        twoGameView.stopUpdateTimer();
    }
}
