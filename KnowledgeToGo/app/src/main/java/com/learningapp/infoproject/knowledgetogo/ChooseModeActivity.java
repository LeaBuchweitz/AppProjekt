package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ChooseModeActivity extends Activity {

    // In inner class needed objects
    private ListView addList;
    private DrawerLayout drawer;
    private ArrayAdapter adapter;
    private ListView lecture_menu;
    //protected int lectureID;
    private ArrayList<String> lectures = new ArrayList<String>();
    private ArrayList<Integer> lectureID = new ArrayList<Integer>();

    private int userID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        // Import layout feature
        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        Button addQuestion = (Button) findViewById(R.id.add_question);
        Button read = (Button) findViewById(R.id.learn);
        addList = (ListView) findViewById(R.id.add_list);
        Button examMode = (Button) findViewById(R.id.exam);
        ImageButton chooseLecture = (ImageButton) findViewById(R.id.choose_lecture);
        chooseLecture.setImageResource(R.drawable.doktor_hut);
        lecture_menu = (ListView) findViewById(R.id.lecture_menu);

        // Check display size for the correct background
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

        // Open Drawer-Menu by clicking on Button
        chooseLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        // Mode just to read all possible questions
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent readQquestion = new Intent(ChooseModeActivity.this, ReadQuestionActivity.class);
                startActivity(readQquestion);
            }
        });

        adapter = new ArrayAdapter<String>(this, R.layout.one_lecture_line, lectures);
        updateDrawer(lectures);
        lecture_menu.setAdapter(adapter);

        // Add ClickListener to enter a special lecture
        lecture_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLecture = (String) lecture_menu.getItemAtPosition(position);

                switch (position) {
                    case 0: {
                        // Logout, return to Login-page and delete session cookie
                        DBVars.SESSION_COOKIE = null;
                        Toast.makeText(ChooseModeActivity.this, "Cookie weg "+ DBVars.SESSION_COOKIE, Toast.LENGTH_LONG).show();
                        Intent backLogin = new Intent(ChooseModeActivity.this, LoginBeginActivity.class);
                        startActivity(backLogin);
                        finish();
                        break;
                    }
                    case 1: {
                        // Add a lecture to the slide menu
                        final EditText enterLecture = new EditText(ChooseModeActivity.this);
                        AlertDialog.Builder newLecture = new AlertDialog.Builder(ChooseModeActivity.this);
                        newLecture.setMessage("Gib den Namen der Vorlesung ein!");
                        newLecture.setView(enterLecture);
                        newLecture.setCancelable(true);
                        newLecture.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String addThisLecture = enterLecture.getText().toString();
                                // Test if Lecture already exists
                                new DatabaseController(DBVars.REQUEST_ALREADY_THERE, "http://android.getenv.net/?mod=Lecture&fun=checkLectureName&name="+addThisLecture,
                                        addThisLecture, userID).start();
                                lectures.clear();
                                updateDrawer(lectures);
                                finish();
                                Intent intent = new Intent(ChooseModeActivity.this, ChooseModeActivity.class);
                                startActivity(intent);

                            }
                        });
                        newLecture.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        newLecture.create().show();
                        break;
                    }
                    case 2: { // Show all available lectures
                        ArrayList<String> allLectures = new ArrayList<String>();
                        DatabaseController db = new DatabaseController(DBVars.REQUEST_LECTURES_DOWNLOAD,
                                "http://android.getenv.net/?mod=Lecture&fun=getLectures",allLectures, lectureID);
                        db.start();
                        while (db.isAlive());
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChooseModeActivity.this);
                        builderSingle.setTitle("Wähle eine Vorlesung der du beitreten möchtest");
                        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(ChooseModeActivity.this,
                                android.R.layout.select_dialog_singlechoice);

                        for(int i = 0; i < allLectures.size(); i++) {
                            listAdapter.add(allLectures.get(i));
                        }
                        builderSingle.setCancelable(true);
                        builderSingle.setNegativeButton("Abbrechen",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderSingle.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String chosen = listAdapter.getItem(which);

                            }
                        });
                        builderSingle.create().show();
                    }
                    default: {} //ToDo alle ID's für Vorlesungen zuordnen
                }
            }
        });

        // Create ArrayList for menu-list of "add"-Button
        ArrayList<String> kindQuestion = new ArrayList<String>();
        kindQuestion.add("Lückentext");
        kindQuestion.add("Stichpunkte");
        kindQuestion.add("Frage bearbeiten");

        // Add list to adapter and into layout
        addList.setAdapter(new ArrayAdapter<String>(ChooseModeActivity.this, R.layout.add_question_line, kindQuestion));

        // Add ClickListener to "add"-Button to show list
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addList.getVisibility() == View.INVISIBLE) {
                    addList.setVisibility(View.VISIBLE);
                } else {
                    addList.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Add a question
        addList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {Intent gap_question = new Intent(ChooseModeActivity.this, Add_Gap_Question_Activity.class);
                             startActivity(gap_question);
                             break;}
                    case 1: {Intent normal_question = new Intent(ChooseModeActivity.this, Add_Normal_Question_Activity.class);
                             startActivity(normal_question);
                             break;}
                    default: {break;}
                }
            }
        });

        // Add ClickListener to 'exam'-Button to go to play mode
        examMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startPlay = new Intent(ChooseModeActivity.this, QuestionModePigActivity.class);
                startActivity(startPlay);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void updateDrawer (ArrayList<String> lectures) {
        lectures.add("Logout");
        lectures.add("Hinzufügen...");
        lectures.add("Suchen...");

        // Make download request for lectures. The result is saved into the given ArrayLists
        DatabaseController db = new DatabaseController(DBVars.REQUEST_LECTURES_DOWNLOAD,
                "http://android.getenv.net/?mod=User&fun=getLectures&uid="+userID,lectures, lectureID);
        db.start();
        while (db.isAlive());
        //lecture_menu.setAdapter(adapter);

    }
}
