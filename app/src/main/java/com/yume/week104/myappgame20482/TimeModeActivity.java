package com.yume.week104.myappgame20482;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yume.week104.myappgame20482.database.SqliteHelper;
import com.yume.week104.myappgame20482.twozerogame.TwoGameView;

import java.util.Timer;
import java.util.TimerTask;


public class TimeModeActivity extends Activity implements Constants, GameView.OnGameStatusChangedListener {
    private static final String TAG = "TimeModeActivity";

    Button menu_TimeMode_Button, restart_TimeMode_Button;
    TextView nowScore_TimeMode_TextView, historyScore_TimeMode_TextView;
    TwoGameView twoGameView_TimeMode;

    SqliteHelper mSqliteHelper;
    SQLiteDatabase mDataBase;

    Handler mHandler;

    Timer mTimer;
    int nowTime = 0;
    int maxTime = 0;
    boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_mode);

        menu_TimeMode_Button = (Button) findViewById(R.id.menu_TimeMode_Button);
        restart_TimeMode_Button = (Button) findViewById(R.id.restart_TimeMode_Button);
        nowScore_TimeMode_TextView = (TextView) findViewById(R.id.nowScore_TimeMode_TextView);
        historyScore_TimeMode_TextView = (TextView) findViewById(R.id.historyScore_TimeMode_TextView);
        twoGameView_TimeMode = (TwoGameView) findViewById(R.id.twoGameView_TimeMode);

        mSqliteHelper = new SqliteHelper(TimeModeActivity.this);
        mDataBase = mSqliteHelper.getWritableDatabase();

        Intent intent = getIntent();
        int index_w = intent.getIntExtra(INTENT_INDEX_W, -1);
        int index_h = intent.getIntExtra(INTENT_INDEX_H, -1);
        if(index_w != -1 && index_h != -1){
            TwoGameView.INDEX_W = index_w;
            TwoGameView.INDEX_H = index_h;
        }

        nowScore_TimeMode_TextView.setText(getTimeString(0));
        menu_TimeMode_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimer != null){
                    mTimer.cancel();
                    mTimer = null;
                }
                new AlertDialog.Builder(TimeModeActivity.this)
                        .setMessage("确定返回主菜单?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SqliteHelper.clearRestoreData(mDataBase);
                                startActivity(new Intent(TimeModeActivity.this, MenuActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!isStart)
                                    return;
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        nowTime++;
                                        mHandler.sendEmptyMessage(0);
                                    }
                                }, 0, 10);
                            }
                        })
                        .create()
                        .show();
            }
        });
        restart_TimeMode_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = false;
                if(mTimer != null){
                    mTimer.cancel();
                    mTimer = null;
                }

                twoGameView_TimeMode.reset();
                twoGameView_TimeMode.setOnGameStatusChangedListener(TimeModeActivity.this);
                nowTime = 0;
                nowScore_TimeMode_TextView.setText(getTimeString(nowTime));
            }
        });

        twoGameView_TimeMode.setMode(MODE_TIME);
        twoGameView_TimeMode.setSeed(GameView.randomProductionSeed());

        maxTime = Integer.valueOf(SqliteHelper.getMaxScore(mDataBase, MODE_TIME, twoGameView_TimeMode.getIndexW(), twoGameView_TimeMode.getIndexH()));
        historyScore_TimeMode_TextView.setText(getTimeString(maxTime));

        twoGameView_TimeMode.setOnGameStatusChangedListener(this);
        twoGameView_TimeMode.startUpdateTimer();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                nowScore_TimeMode_TextView.setText(getTimeString(nowTime));
                historyScore_TimeMode_TextView.setText(getTimeString(maxTime));
            }
        };
    }

    private static String getTimeString(int time){
        int minute = time / 6000;
        int second = time % 6000 / 100;
        int ms = time % 100;
        return String.format("%02d:%02d.%02d", minute, second, ms);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_mode, menu);
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

    @Override
    public void startGame() {
        isStart = true;
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                nowTime++;
                mHandler.sendEmptyMessage(0);
            }
        }, 0, 10);
    }

    @Override
    public void checkFail() {

    }

    @Override
    public void checkSuccess() {

    }

    @Override
    public void checkSuccess(int score) {

    }

    @Override
    public void completeOnRound(boolean have2048) {
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        if(have2048){
            final String title;
            if(maxTime != 0 && nowTime < maxTime){
                title = "新纪录!";
                maxTime = nowTime;
                SqliteHelper.putMaxScore(mDataBase, MODE_TIME, twoGameView_TimeMode.getIndexW(), twoGameView_TimeMode.getIndexH(), maxTime + "");
            }else{
                title = "很可惜";
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(TimeModeActivity.this)
                            .setTitle(title)
                            .setMessage("成绩:\n\n" + getTimeString(nowTime))
                            .setPositiveButton("确定", null)
                            .setCancelable(false)
                            .create()
                            .show();
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(TimeModeActivity.this)
                            .setTitle("GameOver")
                            .setMessage("已经无法移动")
                            .setPositiveButton("确定", null)
                            .setCancelable(false)
                            .create()
                            .show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        twoGameView_TimeMode.stopUpdateTimer();
    }
}
