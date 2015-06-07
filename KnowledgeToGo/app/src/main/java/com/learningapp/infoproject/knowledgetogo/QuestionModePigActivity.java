package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;
import java.util.ArrayList;


public class QuestionModePigActivity extends Activity {

    private DatabaseController db;

    private FlowLayout layout;
    private SurfaceAnimation animation;
    private ArrayList<EditText> editTextList;

    private ArrayList<String> questionContent;
    private ArrayList<Integer> questionType;
    private ArrayList<Integer> questionID;

    private int lectureID;

    private String downloadedText;
    private int questionCounter;
    private int score;
    private int lifes;

    // Indicates which mode: true - shows the question, false - shows the answer
    private boolean modeIsSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lectureID = 1;

        questionCounter = 0;
        questionContent = new ArrayList<>();
        questionType = new ArrayList<>();
        questionID = new ArrayList<>();

        // Make download request for questions. The result is saved into the given ArrayLists
        db = new DatabaseController(DBVars.REQUEST_QUESTION_DOWNLOAD,
                "http://android.getenv.net/?mod=Lecture&fun=getQuestions&lid="+Integer.toString(lectureID),
                questionContent, questionType, questionID);
        db.start();

        setContentView(R.layout.activity_question_mode_pig);

        score = 0;
        lifes = 3;

        layout = (FlowLayout) findViewById(R.id.flow_layout);
        animation = (SurfaceAnimation) findViewById(R.id.surfaceDrawing);

        modeIsSend = true;
        startTask();
    }

    /**
     * Starts first activity
     */
    private void startTask() {
        modeIsSend = true;
        while (db.isAlive());
        questionTask();
    }

    /**
     * Starts next activity
     * @param view context
     */
    public void nextTask(View view){
        if (questionCounter < questionContent.size()) {
            if (modeIsSend) {
                ((Button) findViewById(R.id.button_send)).setText(getString(R.string.button_next));
                modeIsSend = false;
                answerTask();
            } else {
                ((Button) findViewById(R.id.button_send)).setText(getString(R.string.button_send));
                modeIsSend = true;
                questionTask();
            }
        } else {
            endTask();
        }
    }


    /**
     * Creates new question
     */
    public void questionTask(){
        layout.removeAllViews();
        downloadedText = questionContent.get(questionCounter);
        switch(questionType.get(questionCounter)) {
            case DBVars.QUESTION_TYPE_GAPTEXT:
                editTextList = Parser.createGapText(downloadedText, layout, this);
                break;
            case DBVars.QUESTION_TYPE_NOTES:
                editTextList = Parser.createNotes(downloadedText, layout, this);
                break;

        }
    }

    /**
     * OnClick action. Checks Text.
     */
    public void answerTask() {
        ArrayList<String> entries = new ArrayList<>();
        for (EditText text : editTextList) {
            entries.add(text.getText().toString());
        }

        layout.removeAllViews();
        int answerScore = 0;

        switch(questionType.get(questionCounter)) {
            case DBVars.QUESTION_TYPE_GAPTEXT:
                answerScore = Parser.createSolve(downloadedText, entries, layout, this);
                break;
            case DBVars.QUESTION_TYPE_NOTES:
                answerScore = Parser.createNotesSolve(downloadedText, entries, layout, this);
                break;
        }

        score += answerScore * 10;
        animation.getThread().setScore(score);

        if (answerScore == 0){

            lifes--;
            animation.getThread().pigFail();

            if (lifes == 0){
                endTask();
                return;
            }

        } else {
            animation.getThread().pigJump();
        }

        questionCounter++;
    }

    /**
     * When all the answering is done.
     */
    private void endTask() {
        // Go on to Score-Activity if no life or all questions answered
        if(questionCounter == questionContent.size() || lifes == 0) {
            Intent showCurrentScore = new Intent(QuestionModePigActivity.this, ShowScoreActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("Reached-Score", score);
            extras.putInt("Number-Questions", questionCounter);
            showCurrentScore.putExtras(extras);
            QuestionModePigActivity.this.startActivity(showCurrentScore);
            QuestionModePigActivity.this.finish();
        }
    }

    public void pauseAnimation(View view) {
        if (animation.getThread().getRunningState()){
            animation.getThread().pause();
            return;
        }
        animation.getThread().unpause();
    }

    public void jump(View view){
        animation.getThread().pigJump();
    }

}
