package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class ShowScoreActivity extends Activity {

    private int score;
    private int numberQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        // Import layout features
        ImageView scorePig = (ImageView) findViewById(R.id.score_pig);
        TextView runDistance = (TextView) findViewById(R.id.distance);
        TextView encouragement = (TextView) findViewById(R.id.encouragement);

        // Get score-info from QuestionModePigActivity
        Bundle reachedScore = getIntent().getExtras();
        if(reachedScore != null) {
            score = reachedScore.getInt("Reached-Score");
            numberQuestions = reachedScore.getInt("Number-Questions");
        }
        // show correct pig-picture
        if(score == (numberQuestions * 10)) {
            scorePig.setImageResource(R.drawable.schwein_posing);
        } else if(score <= 20) {
            scorePig.setImageResource(R.drawable.schlapp_schwein);
        }

        // Enter the distance
        runDistance.setText(numberQuestions+" km");
        int tmp = score/10;
        if(tmp >= (numberQuestions/4)*3) {
            encouragement.setText("Herzlichen Glückwunsch! Du bist top in Form!");
        } if(tmp >= numberQuestions/2 && tmp < (numberQuestions/4)*3) {
            encouragement.setText("Gut gemacht! Weiter so!");
        } else {
            encouragement.setText("Das war noch nichts! Du musst dringend üben!");
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
}
