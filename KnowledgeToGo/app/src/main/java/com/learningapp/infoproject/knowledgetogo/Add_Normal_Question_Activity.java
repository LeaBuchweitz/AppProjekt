package com.learningapp.infoproject.knowledgetogo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Add_Normal_Question_Activity extends ActionBarActivity {

    private EditText question;
    private EditText firstAnswer;
    private EditText secondAnswer;
    private EditText thirdAnswer;
    private EditText forthAnswer;
    private int numberAnswers;
    private boolean enoughAnswers;
    private SharedPreferences prefs;

    private int lid;
    private int uid;
    private boolean uploaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__normal__question_);

        uploaded = false;

        // Add layout features
        Button sendToServer = (Button) findViewById(R.id.save_question);
        Button addAnswer = (Button) findViewById(R.id.add_answer);
        Button lessAnswer = (Button) findViewById(R.id.less_answer);
        question = (EditText) findViewById(R.id.question);
        firstAnswer = (EditText) findViewById(R.id.first_answer);
        secondAnswer = (EditText) findViewById(R.id.second_answer);
        thirdAnswer = (EditText) findViewById(R.id.third_answer);
        forthAnswer = (EditText) findViewById(R.id.forth_answer);
        numberAnswers = 1;
        enoughAnswers = false;

        // Get Info from SharedPreferences for User-ID
        prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        uid = prefs.getInt("User-ID",0);
        lid = prefs.getInt("Lecture-ID",0);

        // DialogBox with instructions how to mark a gap
        if(prefs.getBoolean("Instruction",true)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_normal_question, null);
            dialog.setCancelable(true);
            dialog.setView(view);
            final CheckBox dontShow = (CheckBox) view.findViewById(R.id.dont_show);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Don't show dalogBox again, if CheckBox marked
                    if(dontShow.isChecked()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("Instruction", false);
                        editor.apply();
                    }
                    dialog.dismiss();
                }
            });
            dialog.create().show();
        }

        // Add answer-possibilities
        addAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!enoughAnswers) {
                    switch (numberAnswers) {
                        case 1: {
                            secondAnswer.setVisibility(View.VISIBLE);
                            numberAnswers++;
                            break;
                        }
                        case 2: {
                            thirdAnswer.setVisibility(View.VISIBLE);
                            numberAnswers++;
                            break;
                        }
                        case 3: {
                            forthAnswer.setVisibility(View.VISIBLE);
                            numberAnswers++;
                            break;
                        }
                        case 4: {
                            enoughAnswers = true;
                            break;
                        }
                    }
                }
            }
        });

        // Remove answer-possibilities
        lessAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    switch (numberAnswers) {
                            case 2: {
                                secondAnswer.setVisibility(View.GONE);
                                numberAnswers--;
                                break;
                            }
                            case 3: {
                                thirdAnswer.setVisibility(View.GONE);
                                numberAnswers--;
                                break;
                            }
                            case 4: {
                                forthAnswer.setVisibility(View.GONE);
                                enoughAnswers = false;
                                numberAnswers--;
                                break;
                            }
                            default: {break;}
                        }
                    }
        });

        sendToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendQuestion();

            }
        });
    }

    public void sendQuestion(){
        if (uploaded){
            AlertDialog.Builder notValid = new AlertDialog.Builder(this);
            notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            notValid.setMessage(R.string.already_uploaded);
            notValid.create().show();
            return;
        }

        // Get info from EditText
        String upload = "";
        String q = question.getText().toString();
        String a1 = firstAnswer.getText().toString();
        String a2 = secondAnswer.getText().toString();
        String a3 = thirdAnswer.getText().toString();
        String a4 = forthAnswer.getText().toString();
        switch (numberAnswers) {
            case 4: if (!a4.equals("")) upload = "{" + forthAnswer.getText().toString() + "}" + upload;
            case 3: if (!a3.equals("")) upload = "{" + thirdAnswer.getText().toString() + "}" + upload;
            case 2: if (!a2.equals("")) upload = "{" + secondAnswer.getText().toString() + "}" + upload;
            case 1: if (!a1.equals("")) upload = "{" + firstAnswer.getText().toString() + "}" + upload;
                upload = q + upload;
        }

        if (!Parser.checkParenthesis(upload)) {
            AlertDialog.Builder notValid = new AlertDialog.Builder(this);
            notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            notValid.setMessage(R.string.notesInputNotCorrect);
            notValid.create().show();
            return;
        }

        // Upload evrythin'
        if (!upload.equals(q) && Parser.checkHeading(q)) {
            ArrayList<Integer> check = new ArrayList<Integer>();
            DatabaseController db = new DatabaseController(DBVars.REQUEST_QUESTION_INSERT,
                    "http://android.getenv.net/?mod=Lecture&fun=insertQuestion&type=" +
                            DBVars.QUESTION_TYPE_NOTES +
                            "&lid=" + lid + "&uid=" + uid + "&content=" + upload,
                    check);
            db.start();
            while(db.isAlive());
            if (check.size()==1){
                Toast.makeText(this, R.string.Upload_succesfull, Toast.LENGTH_LONG).show();
                uploaded = true;
            } else {
                Toast.makeText(this, R.string.Upload_unsuccesfull, Toast.LENGTH_LONG).show();
            }
        } else {
            AlertDialog.Builder notValid = new AlertDialog.Builder(this);
            notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            notValid.setMessage(R.string.atLeastOneAnswer);
            notValid.create().show();
            return;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(Add_Normal_Question_Activity.this, ChooseModeActivity.class);
        startActivity(intent);
        finish();
    }

}
