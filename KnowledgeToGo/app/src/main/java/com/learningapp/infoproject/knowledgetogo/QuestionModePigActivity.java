package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apmem.tools.layouts.FlowLayout;
import java.util.ArrayList;


public class QuestionModePigActivity extends Activity {

    private FlowLayout layout;
    private SurfaceAnimation animation;
    private ArrayList<EditText> editTextList;

    private String downloadedText;
    private int score;
    private boolean modeIsSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_mode_pig);

        score = 0;

        layout = (FlowLayout) findViewById(R.id.flow_layout);
        animation = (SurfaceAnimation) findViewById(R.id.surfaceDrawing);

        modeIsSend = true;
        nextTask();


    }

    /**
     * OnClick action. Checks Text.
     */
    public void checkText() {
        ArrayList<String> entries = new ArrayList<>();
        for (EditText text : editTextList) {
            entries.add(text.getText().toString());
        }

        layout.removeAllViews();
        score += Parser.createSolve(downloadedText, entries, layout, this);
        animation.getThread().setScore(score);
    }

    public void nextTask(View view){
        if (modeIsSend){
            ((Button) findViewById(R.id.button_send)).setText(getString(R.string.button_next));
            modeIsSend = false;
            checkText();
        } else {
            ((Button) findViewById(R.id.button_send)).setText(getString(R.string.button_send));
            modeIsSend = true;
            nextTask();
        }
    }

    public void nextTask(){
        layout.removeAllViews();
        downloadedText = "Ach, das {ist} ein toller {Lückentext}. afdasfdsa dfasf asdf asfkjdsa huasd hbfdasfuzi adhsjufi ahfudasuf iash fdhuasi hfdaso {fdas} fdsa ";
        editTextList = Parser.createGapText(downloadedText, layout, this);
    }

    public void pauseAnimation(View view) {
        if (animation.getThread().getRunningState()){
            animation.getThread().pause();
            return;
        }
        animation.getThread().unpause();
    }

    public void jump(View view){
        animation.getThread().pigJump(1000,1000);
    }

}
