package com.learningapp.infoproject.knowledgetogo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class BestUserActivity extends ActionBarActivity {

    private DatabaseController db;
    // Get Info from SharedPreferences for User-ID
    private SharedPreferences prefs;
    private int uid;
    private int lid;

    static final int TEXT_PADDING = 5;
    private float score;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<Integer> scores = new ArrayList<Integer>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_user2);

        // Import layout features
        //LinearLayout scoreList = (LinearLayout) findViewById(R.id.bestUserList);
        TextView bestUser = (TextView) findViewById(R.id.bestUser);
        TextView second = (TextView) findViewById(R.id.second);
        TextView third = (TextView) findViewById(R.id.third);
        ImageView crown = (ImageView) findViewById(R.id.crown);
        ImageView crownSilver = (ImageView) findViewById(R.id.crown_silver);
        ImageView crownBronze = (ImageView) findViewById(R.id.crown_bronze);
        crown.setImageResource(R.drawable.best_user);
        crownSilver.setImageResource(R.drawable.crown_silver);
        crownBronze.setImageResource(R.drawable.crown_bronze);

        Bundle reachedScore = getIntent().getExtras();
        if(reachedScore != null) {
            score = reachedScore.getInt("Reached-Score");
            names = reachedScore.getStringArrayList("names");
            scores = reachedScore.getIntegerArrayList("scores");
        }

        // Get Info from SharedPreferences for User-ID
        SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        uid = prefs.getInt("User-ID",0);
        lid = prefs.getInt("Lecture-ID", 0);

        // Adds best users
        switch (names.size()) {
            case 1: bestUser.setText(names.get(0) + ": " + scores.get(0));
                    second.setText("-");
                    third.setText("-");
                    break;
            case 2: bestUser.setText(names.get(0) + ": " + scores.get(0));
                    second.setText(names.get(1) + ": " + scores.get(1));
                    third.setText("-");
                    break;
            default: bestUser.setText(names.get(0) + ": " + scores.get(0));
                    second.setText(names.get(1) + ": " + scores.get(1));
                    third.setText(names.get(2) + ": " + scores.get(2));
                    break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_best_user, menu);
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

    public void onBackPressed() {
        Intent intent = new Intent(BestUserActivity.this, ChooseModeActivity.class);
        startActivity(intent);
        finish();
    }
}
