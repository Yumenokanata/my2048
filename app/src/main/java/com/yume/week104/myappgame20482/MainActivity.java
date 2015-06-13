package com.yume.week104.myappgame20482;

import android.app.Activity;
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


public class MainActivity extends Activity implements Constants {
    private static final String TAG = "MainActivity";
    public static final int RESULT_RELOAD = 0x11;

    TextView nowScore_TextView, historyScore_TextView;
    Button menu_Button;
    TwoGameView twoGameView;

    SqliteHelper mSqliteHelper;
    SQLiteDatabase mDataBase;

    int mMaxScore = 0;
    int mNowScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoGameView = (TwoGameView) findViewById(R.id.twoGameView);
        nowScore_TextView = (TextView) findViewById(R.id.nowScore_TextView);
        historyScore_TextView = (TextView) findViewById(R.id.historyScore_TextView);
        menu_Button = (Button) findViewById(R.id.menu_Button);

        mSqliteHelper = new SqliteHelper(MainActivity.this);
        mDataBase = mSqliteHelper.getWritableDatabase();

        mMaxScore = SqliteHelper.getMaxScore(mDataBase);

        nowScore_TextView.setText("0");
        historyScore_TextView.setText(mMaxScore + "");
        menu_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, MenuActivity.class), 1);
            }
        });

        twoGameView.setSeed(GameView.randomProductionSeed());
        SqliteHelper.RestoreData restoreData = SqliteHelper.getRestoreData(mDataBase);
        if(restoreData.data != null){
            twoGameView.setData(restoreData.data);
            nowScore_TextView.setText(restoreData.score + "");
        }

        twoGameView.setOnGameStatusChangedListener(new GameView.OnGameStatusChangedListener() {
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
            public void completeOnRound() {
                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                intent.putExtra(INTENT_SCORE, mNowScore);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
                twoGameView.reset();
                mNowScore = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nowScore_TextView.setText(mNowScore + "");
                        historyScore_TextView.setText(mMaxScore + "");
                    }
                });
            }
        });
        twoGameView.startUpdateTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_RELOAD){
            twoGameView.reset();
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
        SqliteHelper.putMaxScore(mDataBase, mMaxScore);
        String restoreData = twoGameView.getDataString();
//        Log.d(TAG, "onStop restoreData: " + restoreData);
        SqliteHelper.putRestoreData(mDataBase, new SqliteHelper.RestoreData(restoreData, mNowScore));
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
}
