package com.yume.week104.myappgame20482;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yume.week104.myappgame20482.database.SqliteHelper;
import com.yume.week104.myappgame20482.twozerogame.TwoGameView;


public class MenuActivity extends Activity implements View.OnClickListener, Constants {
    Button normal_mode_button, obstacle_mode_button, time_mode_button, crazy_mode_button, custom_button;

    int MAX_INDEX_W = -1;
    int MAX_INDEX_H = -1;

    SqliteHelper mSqliteHelper;
    SQLiteDatabase mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        normal_mode_button = (Button) findViewById(R.id.normal_mode_button);
        obstacle_mode_button = (Button) findViewById(R.id.obstacle_mode_button);
        time_mode_button = (Button) findViewById(R.id.time_mode_button);
        crazy_mode_button = (Button) findViewById(R.id.crazy_mode_button);
        custom_button = (Button) findViewById(R.id.custom_button);

        normal_mode_button.setOnClickListener(this);
        obstacle_mode_button.setOnClickListener(this);
        time_mode_button.setOnClickListener(this);
        crazy_mode_button.setOnClickListener(this);
        custom_button.setOnClickListener(this);

        mSqliteHelper = new SqliteHelper(MenuActivity.this);
        mDataBase = mSqliteHelper.getWritableDatabase();
    }

    @Override
    public void onClick(View v) {
        Integer mode = MODE_NORMAL;

        switch (v.getId()){
            case R.id.custom_button:
                View view = LayoutInflater.from(MenuActivity.this).inflate(R.layout.custom_dialog_layout, null);
                final EditText column_num_EditText = (EditText) view.findViewById(R.id.column_num_EditText);
                final EditText row_num_EditText = (EditText) view.findViewById(R.id.row_num_EditText);

                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("请设定行数和列数")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int column;
                                int row;
                                try{
                                    column = Integer.valueOf(column_num_EditText.getText().toString());
                                    row = Integer.valueOf(row_num_EditText.getText().toString());
                                    if(column < 3 || column > 7 || row < 3 || row > 7)
                                        throw new NumberFormatException();
                                }catch (NumberFormatException e){
                                    Toast.makeText(MenuActivity.this, "输入有误", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                MAX_INDEX_W = column;
                                MAX_INDEX_H = row;
                                TwoGameView.INDEX_W = MAX_INDEX_W;
                                TwoGameView.INDEX_H = MAX_INDEX_H;
                                SqliteHelper.clearRestoreData(mDataBase);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                return;
            case R.id.time_mode_button:
                mode = MODE_TIME;
                Intent intent = new Intent(MenuActivity.this, TimeModeActivity.class);
                intent.putExtra(INTENT_MODE, mode);
                intent.putExtra(INTENT_INDEX_W, MAX_INDEX_W);
                intent.putExtra(INTENT_INDEX_H, MAX_INDEX_H);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
                return;
            case R.id.normal_mode_button:
                mode = MODE_NORMAL;
                break;
            case R.id.obstacle_mode_button:
                mode = MODE_OBSTACLE;
                break;
            case R.id.crazy_mode_button:
                mode = MODE_CRAZY;
                break;
        }
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra(INTENT_MODE, mode);
        intent.putExtra(INTENT_INDEX_W, MAX_INDEX_W);
        intent.putExtra(INTENT_INDEX_H, MAX_INDEX_H);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
