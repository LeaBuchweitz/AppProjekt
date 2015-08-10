package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ShowScoreActivity extends Activity {

    private DatabaseController db;

    private float score;
    private float numberQuestions;

    private int lectureId;
    private int userID;

    private ArrayList<String> names;
    private ArrayList<Integer> scores;
    private ArrayList<Integer> ids;

    static final int TEXT_PADDING = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        // Import layout features
        ImageView scorePig = (ImageView) findViewById(R.id.score_pig);
        TextView runDistance = (TextView) findViewById(R.id.distance);
        runDistance.setGravity(Gravity.CENTER);
        TextView encouragement = (TextView) findViewById(R.id.encouragement);
        encouragement.setGravity(Gravity.CENTER);
        TextView rang = (TextView) findViewById(R.id.your_rang);
        final Button best = (Button) findViewById(R.id.best_button);


        // Check display size for the correct background
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        if (height <= 800) {
            RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, -120, 0, -160);
            scorePig.setScaleX((float) 0.5);
            scorePig.setScaleY((float) 0.5);
            scorePig.setLayoutParams(llp);
        }

        names = new ArrayList<>();
        scores = new ArrayList<>();
        ids = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        lectureId = prefs.getInt("Lecture-ID", -1);
        userID = prefs.getInt("User-ID",0);

        // Get score-info from QuestionModePigActivity
        Bundle reachedScore = getIntent().getExtras();
        if(reachedScore != null) {
            score = reachedScore.getInt("Reached-Score");
            numberQuestions = reachedScore.getInt("Number-Questions");
        }

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
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("names", names);
                bundle.putIntegerArrayList("scores", scores);
                Intent bestUser = new Intent(ShowScoreActivity.this, BestUserActivity.class);
                bestUser.putExtra("Reached-Score", score);
                bestUser.putExtras(bundle);
                startActivity(bestUser);
            }
        });


        db = new DatabaseController(DBVars.REQUEST_UPLOAD_SCORE,
                "http://android.getenv.net/?mod=User&fun=setScore&lid="+lectureId+"&uid="+userID+"&score="+score);
        db.start();

        while (db.isAlive());

        // Show best users of this lecture
        db = new DatabaseController(DBVars.REQUEST_BEST_USER,
                "http://android.getenv.net/?mod=Lecture&fun=getBestUser&lid="+lectureId, names, scores, ids);
        db.start();
        while (db.isAlive());

        for(int i = 0; i < names.size(); i++) {
            if(ids.get(i).equals(userID)) {
                rang.setText(i+1 + ".");
            }
        }
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

    public void onBackPressed() {
        Intent intent = new Intent(ShowScoreActivity.this, ChooseModeActivity.class);
        startActivity(intent);
        finish();
    }
}
