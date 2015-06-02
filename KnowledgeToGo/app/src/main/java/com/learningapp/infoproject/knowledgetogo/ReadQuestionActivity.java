package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;


public class ReadQuestionActivity extends Activity {

    private FlowLayout layout;

    private ArrayList<String> questionContent;
    private ArrayList<Integer> questionType;
    private ArrayList<Integer> questionID;

    private int lectureID;

    private String downloadedText;
    private int questionCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lectureID = 1;

        questionCounter = 0;
        questionContent = new ArrayList<>();
        questionType = new ArrayList<>();
        questionID = new ArrayList<>();

        // Make download request for questions. The result is saved into the given ArrayLists
        new DatabaseController(DBVars.REQUEST_QUESTION_DOWNLOAD,
                "http://android.getenv.net/?mod=Lecture&fun=getQuestions&lid="+Integer.toString(lectureID),
                questionContent, questionType, questionID).start();

        setContentView(R.layout.activity_read_question);

        layout = (FlowLayout) findViewById(R.id.flow_layout);

        startTask();
    }


    public void startTask(){
        while (questionContent.size() == 0);
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

                break;
        }
    }
}
