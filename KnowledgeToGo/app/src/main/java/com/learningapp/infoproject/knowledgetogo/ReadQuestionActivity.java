package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;


public class ReadQuestionActivity extends Activity {

    private FlowLayout layout;

    private ArrayList<String> questionContent;
    private ArrayList<Integer> questionType;
    private ArrayList<Integer> questionID;
    private ImageView back;
    private ImageView goOn;

    private int lectureID;

    private String downloadedText;
    private int questionCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_question);

        // Get Lecture-ID from selected lecture
        SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        lectureID = prefs.getInt("Lecture-ID", -1);

        // Check display size for the correct background
        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if(width > 600 && height > 800) {
            background.setBackgroundResource(R.drawable.background2);
        } else {
            background.setBackgroundResource(R.drawable.background);
        }

        questionCounter = 0;
        questionContent = new ArrayList<>();
        questionType = new ArrayList<>();
        questionID = new ArrayList<>();

            // Make download request for questions. The result is saved into the given ArrayLists
            DatabaseController db = new DatabaseController(DBVars.REQUEST_QUESTION_DOWNLOAD,
                    "http://android.getenv.net/?mod=Lecture&fun=getQuestions&lid=" + Integer.toString(lectureID),
                    questionContent, questionType, questionID);
            db.start();
            while (db.isAlive()) ;

        layout = (FlowLayout) findViewById(R.id.flow_layout);

        // Import Layout Features
        back = (ImageView) findViewById(R.id.go_back);
        goOn = (ImageView) findViewById(R.id.go_on);
        back.setImageResource(R.drawable.go_back);
        goOn.setImageResource(R.drawable.go_on);

        startTask();

        startTask();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backTask(v);
            }
        });

        goOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTask(v);
            }
        });
    }

    public void startTask(){

        if (questionContent.size() == 1 && questionContent.get(0).equals("No-Question")){
            AlertDialog.Builder noSelectedLecture = new AlertDialog.Builder(ReadQuestionActivity.this);
            noSelectedLecture.setTitle("Die Vorlesung enthält keine Fragen!");
            noSelectedLecture.setMessage("Erstelle doch welche.");
            noSelectedLecture.setCancelable(false);
            noSelectedLecture.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(ReadQuestionActivity.this, ChooseModeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            noSelectedLecture.create().show();
            questionContent.add("Keine Fragen Vorhanden.");
            questionType.add(DBVars.QUESTION_TYPE_GAPTEXT);
        }

        createText();
    }

    public void nextTask(View view){
        if (questionCounter + 1 < questionContent.size()) {
            questionCounter++;
            createText();
        }
    }

    public void backTask(View view){
        if (questionCounter > 0) {
            questionCounter--;
            createText();
        }
    }

    private void createText(){
        layout.removeAllViews();
        downloadedText = questionContent.get(questionCounter);
        switch(questionType.get(questionCounter)) {
            case DBVars.QUESTION_TYPE_GAPTEXT:
                Parser.createText(downloadedText, layout, this);
                break;
            case DBVars.QUESTION_TYPE_NOTES:
                Parser.createNotesText(downloadedText, layout, this);
                break;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(ReadQuestionActivity.this, ChooseModeActivity.class);
        startActivity(intent);
        finish();
    }
}
