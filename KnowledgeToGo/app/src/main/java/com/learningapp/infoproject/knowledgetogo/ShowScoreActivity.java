package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ShowScoreActivity extends Activity {

    private DatabaseController db;

    private float score;
    private float numberQuestions;

    private int lectureId=1;
    private int userID;

    private ArrayList<String> names;
    private ArrayList<Integer> scores;

    static final int TEXT_PADDING = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        // Import layout features
        LinearLayout scoreList = (LinearLayout) findViewById(R.id.bestUserList);
        ImageView scorePig = (ImageView) findViewById(R.id.score_pig);
        ImageView crown = (ImageView) findViewById(R.id.crown);
        TextView runDistance = (TextView) findViewById(R.id.distance);
        TextView encouragement = (TextView) findViewById(R.id.encouragement);
        TextView bestUser = (TextView) findViewById(R.id.bestUser);
        crown.setImageResource(R.drawable.best_user);
        final Button best = (Button) findViewById(R.id.best_button);

        // Get score-info from QuestionModePigActivity
        Bundle reachedScore = getIntent().getExtras();
        if(reachedScore != null) {
            score = reachedScore.getInt("Reached-Score");
            numberQuestions = reachedScore.getInt("Number-Questions");
        }

        // Get Info from SharedPreferences for User-ID
        SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        userID = prefs.getInt("User-ID",0);

        names = new ArrayList<>();
        scores = new ArrayList<>();


        float tmp = score/10;

        // Enter the distance
        runDistance.setText(tmp +" km");
        // If right answers are at least 1/2 of all questions available
        if(tmp >= numberQuestions/2) {
            // If right answers are at least 3/4 of all questions available
            if(tmp > (numberQuestions/4)*3) {
                encouragement.setText("Herzlichen Glückwunsch! Du bist top in Form!");
                scorePig.setImageResource(R.drawable.schwein_posing);
            } else {
                encouragement.setText("Gut gemacht! Weiter so!");
                scorePig.setImageResource(R.drawable.schwein_steht);
            }
        } else {
            // If right answers are less than 1/4 of all questions available
            if (tmp > numberQuestions/4) {
                encouragement.setText("Übung macht den Meister! Bleib' dran!");
                scorePig.setImageResource(R.drawable.schwein_steht);
            } else {
                encouragement.setText("Das war noch nichts! Du musst dringend üben!");
                scorePig.setImageResource(R.drawable.schlapp_schwein);
            }
        }

        best.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bestUser = new Intent(ShowScoreActivity.this, BestUserActivity.class);
                bestUser.putExtra("Reached-Score", score);
                startActivity(bestUser);
            }
        });


        db = new DatabaseController(DBVars.REQUEST_UPLOAD_SCORE,
                "http://android.getenv.net/?mod=User&fun=setScore&lid="+lectureId+"&uid="+userID+"&score="+score);
        db.start();

        while (db.isAlive());

        /*db = new DatabaseController(DBVars.REQUEST_BEST_USER,
                "http://android.getenv.net/?mod=Lecture&fun=getBestUser&lid="+lectureId, names, scores);
        db.start();

        while (db.isAlive());

        bestUser.setText(names.get(0) + ": " + scores.get(0));

        // Adds best users
        for(int i = 1; i < names.size(); i++){
            TextView text = new TextView(this);
            text.setText((i+1)+". "+names.get(i) + ": " + scores.get(i));
            text.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            text.setTextSize(20);
            text.setTextColor(Color.WHITE);
            text.setTypeface(null, Typeface.BOLD);
            scoreList.addView(text);
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_score, menu);
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
