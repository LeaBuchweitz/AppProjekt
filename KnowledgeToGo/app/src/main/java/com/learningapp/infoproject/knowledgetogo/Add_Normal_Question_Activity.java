package com.learningapp.infoproject.knowledgetogo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__normal__question_);

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
                // Get info from EditText
                String addedQuestion = question.getText().toString();
                String firstWord = firstAnswer.getText().toString();
                String secondWord = secondAnswer.getText().toString();
                String thirdWord = thirdAnswer.getText().toString();
                String forthWord = forthAnswer.getText().toString();

                // Get info of the question together into a bundle
                Bundle question = new Bundle();
                ArrayList<String> titleAnswers = new ArrayList<String>();
                if(addedQuestion.equals("") | firstWord.equals("")) {
                    Toast.makeText(Add_Normal_Question_Activity.this, R.string.no_question_answer, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    titleAnswers.add(addedQuestion);
                    titleAnswers.add(firstWord);
                } if(!secondWord.equals("")) {
                    titleAnswers.add(secondWord);
                } if(!thirdWord.equals("")) {
                    titleAnswers.add(thirdWord);
                } if(!forthWord.equals("")) {
                    titleAnswers.add(forthWord);
                }
                question.putStringArrayList("Question", titleAnswers);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add__normal__question_, menu);
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
