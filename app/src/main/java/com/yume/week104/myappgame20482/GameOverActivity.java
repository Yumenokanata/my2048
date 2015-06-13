package com.yume.week104.myappgame20482;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GameOverActivity extends Activity implements Constants {
    TextView score_TextView;
    Button restart_gameover_button, back_gameover_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score_TextView = (TextView) findViewById(R.id.score_TextView);
        restart_gameover_button = (Button) findViewById(R.id.restart_gameover_button);
        back_gameover_button = (Button) findViewById(R.id.back_gameover_button);

        Intent intent = getIntent();
        score_TextView.setText(intent.getIntExtra(INTENT_SCORE, 0) + "");

        restart_gameover_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(MainActivity.RESULT_RELOAD);
                finish();
                overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
            }
        });
        back_gameover_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(MainActivity.RESULT_BACK);
                finish();
                overridePendingTransition(R.anim.activity_in_anim, R.anim.activity_out_anim);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_over, menu);
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
