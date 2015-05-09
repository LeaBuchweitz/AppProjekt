package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class QuestionModePigActivity extends Activity {

    private ArrayList<EditText> editTextList;

    private LinearLayout linearLayout;

    private String downloadedText;

    private int score;

    private SurfaceAnimation animation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_mode_pig);

        score = 0;

        linearLayout = (LinearLayout) findViewById(R.id.question_layout);
        animation = (SurfaceAnimation) findViewById(R.id.surfaceDrawing);

        nextTask();


    }

    /**
     * OnClick action. Checks Text.
     *
     * @param view Context
     */
    public void checkText(View view) {
        ArrayList<String> entries = new ArrayList<>();
        for (EditText text : editTextList) {
            entries.add(text.getText().toString());
        }

        linearLayout.removeAllViews();
        score += Parser.createSolve(downloadedText, entries, linearLayout, this);
        animation.getThread().setScore(score);
    }

    public void nextTask(View view){
        nextTask();
    }

    public void nextTask(){
        linearLayout.removeAllViews();
        downloadedText = "Ach, das {ist} ein toller {LÃ¼ckentext}.";
        editTextList = Parser.createGapText(downloadedText, linearLayout, this);
    }

    public void pauseAnimation(View view) {
        if (animation.getThread().getRunningState()){
            animation.getThread().pause();
            return;
        }
        animation.getThread().unpause();
    }

}
