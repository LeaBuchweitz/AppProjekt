package com.learningapp.infoproject.knowledgetogo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ChooseModeActivity extends Activity {

    // In inner class needed objects
    private ListView addList;
    private DrawerLayout drawer;
    private ArrayAdapter adapter;
    private ListView lecture_menu;
    private ArrayList<String> lectures = new ArrayList<String>();
    private ArrayList<Integer> lectureID = new ArrayList<Integer>();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private ArrayList<String> questionContent;
    private ArrayList<Integer> questionType;
    private ArrayList<Integer> questionID;

    private SurfaceAnimationMenu menuBackground;

    private int uid;
    private int lid;


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
        final Button examMode = (Button) findViewById(R.id.exam);
        ImageButton chooseLecture = (ImageButton) findViewById(R.id.choose_lecture);
        chooseLecture.setImageResource(R.drawable.doktor_hut);
        lecture_menu = (ListView) findViewById(R.id.lecture_menu);

        menuBackground = (SurfaceAnimationMenu) findViewById(R.id.surfaceDrawing);

        // Get Info from SharedPreferences for User-ID
        SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        lid = prefs.getInt("Lecture-ID", -1);
        uid = prefs.getInt("User-ID", 0);

        // Bird sound in background
        mMediaPlayer = MediaPlayer.create(this, R.raw.bird);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        // Check display size for the correct background
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (width > 600 && height > 800) {
            background.setBackgroundResource(R.drawable.background2);
        } else {
            background.setBackgroundResource(R.drawable.background);
        }

        // Open Drawer-Menu by clicking on Button
        chooseLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);

                colorizeSelected();

            }
        });

        // Mode just to read all possible questions
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If no lecture is selected
                if (lid == -1) {
                    AlertDialog.Builder noSelectedLecture = new AlertDialog.Builder(ChooseModeActivity.this);
                    noSelectedLecture.setTitle("Keine Vorlesung gewählt!");
                    noSelectedLecture.setMessage("Bitte wähle in deinem Vorlesungsmenü eine Vorlesung aus!");
                    noSelectedLecture.setCancelable(true);
                    noSelectedLecture.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    noSelectedLecture.create().show();
                } else {
                    Intent readQquestion = new Intent(ChooseModeActivity.this, ReadQuestionActivity.class);
                    startActivity(readQquestion);
                    killBackground();
                    mMediaPlayer.stop();
                }
            }
        });

        adapter = new ArrayAdapter<String>(this, R.layout.one_lecture_line, lectures);
        updateDrawer();

        lecture_menu.setAdapter(adapter);

        drawer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                colorizeSelected();
                return false;
            }
        });

        // Add ClickListener to enter a special lecture
        lecture_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedLecture = (String) lecture_menu.getItemAtPosition(position);

                SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);

                switch (position) {
                    case 0: {
                        // Delete User-ID from Shared Preferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("User-ID");
                        editor.remove("Lecture-ID");
                        editor.remove("Selected-Lecture");
                        editor.apply();

                        // Logout, return to Login-page and delete session cookie
                        DBVars.SESSION_COOKIE = null;
                        Intent backLogin = new Intent(ChooseModeActivity.this, LoginBeginActivity.class);
                        startActivity(backLogin);
                        killBackground();
                        mMediaPlayer.stop();
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
                                new DatabaseController(DBVars.REQUEST_ALREADY_THERE, "http://android.getenv.net/?mod=Lecture&fun=checkLectureName&name=" + addThisLecture,
                                        addThisLecture, uid).start();
                                updateDrawer();
                                finish();
                                Intent intent = new Intent(ChooseModeActivity.this, ChooseModeActivity.class);
                                startActivity(intent);
                                killBackground();

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
                                "http://android.getenv.net/?mod=Lecture&fun=getLectures", allLectures, lectureID);
                        db.start();
                        while (db.isAlive()) ;
                        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChooseModeActivity.this);
                        builderSingle.setTitle("Wähle eine Vorlesung der du beitreten möchtest");
                        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(ChooseModeActivity.this,
                                android.R.layout.select_dialog_singlechoice);

                        for (int i = 0; i < allLectures.size(); i++) {
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
                        // New drawer if you choose a new lecture you want to be part of
                        builderSingle.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get lecture-ID and set a new score for the chosen lecture
                                String newLecture = listAdapter.getItem(which);
                                ArrayList<Integer> lID = new ArrayList<Integer>();
                                DatabaseController db = new DatabaseController(DBVars.REQUEST_INSERT_SCORE,
                                        "http://android.getenv.net/?mod=Lecture&fun=lectureID&name=" + newLecture, lID, uid);
                                db.start();
                                drawer.closeDrawers();
                                while (db.isAlive()) ;
                                Toast.makeText(ChooseModeActivity.this, "Du hast dich für " + newLecture + " registriert!", Toast.LENGTH_LONG).show();

                                // Set LID
                                lid = lID.get(0);
                                lectures.removeAll(lectures);
                                updateDrawer();
                            }
                        });
                        builderSingle.create().show();
                        break;
                    }
                    default: {
                        // Delete all scores from User
                        if (selectedLecture.equals("Zurücksetzen")) {
                            AlertDialog.Builder deleteAll = new AlertDialog.Builder(ChooseModeActivity.this);
                            deleteAll.setTitle("Bist du sicher?");
                            deleteAll.setMessage("Es werden alle Einstellungen zurück gesetzt und alle Scores gelöscht!");
                            deleteAll.setCancelable(true);
                            deleteAll.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            deleteAll.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.remove("Instruction");
                                    editor.remove("Selected-Lecture");
                                    editor.apply();
                                    DatabaseController db = new DatabaseController(DBVars.REQUEST_DELETE_SCORES,
                                            "http://android.getenv.net/?mod=User&fun=deleteScores&uid=" + prefs.getInt("User-ID", 0));
                                    db.start();
                                    drawer.closeDrawers();
                                    while (db.isAlive()) ;
                                    Toast.makeText(ChooseModeActivity.this, R.string.deleted, Toast.LENGTH_LONG).show();
                                    lectures.removeAll(lectures);
                                    updateDrawer();
                                }
                            });
                            deleteAll.create().show();
                        } else {
                            ArrayList<Integer> lID = new ArrayList<Integer>();
                            DatabaseController db = new DatabaseController(DBVars.REQUEST_GET_LECTURE_ID,
                                    "http://android.getenv.net/?mod=Lecture&fun=lectureID&name=" + selectedLecture, lID, uid);
                            db.start();
                            drawer.closeDrawers();
                            while (db.isAlive()) ;
                            if (lID.size() > 0)
                                lid = lID.get(0);

                            SharedPreferences.Editor editor = prefs.edit();
                            int posit = prefs.getInt("Selected-Lecture", -1);
                            editor.putInt("Lecture-ID", lid);

                            if (posit != -1) {
                                View v2 = lecture_menu.getChildAt(posit);
                                TextView oldLecture = (TextView) v2.findViewById(R.id.menu_item);
                                oldLecture.setTextColor(Color.WHITE);
                            }
                            editor.putInt("Selected-Lecture", position);
                            editor.commit();

                            // Set color to selected lecture
                            colorizeSelected();
                        }
                        break;
                    }
                }
            }
        });

        // Create ArrayList for menu-list of "add"-Button
        ArrayList<String> kindQuestion = new ArrayList<String>();
        kindQuestion.add("Lückentext");
        kindQuestion.add("Stichpunkte");

        // Add list to adapter and into layout
        addList.setAdapter(new ArrayAdapter<String>(ChooseModeActivity.this, R.layout.add_question_line, kindQuestion));

        // Add ClickListener to "add"-Button to show list
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addList.getVisibility() == View.INVISIBLE) {
                    addList.setVisibility(View.VISIBLE);
                    examMode.setVisibility(View.INVISIBLE);
                } else {
                    addList.setVisibility(View.INVISIBLE);
                    examMode.setVisibility(View.VISIBLE);
                }
            }
        });

        // Add a question
        addList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        // If no lecture is selected
                        if (lid == -1) {
                            AlertDialog.Builder noSelectedLecture = new AlertDialog.Builder(ChooseModeActivity.this);
                            noSelectedLecture.setTitle("Keine Vorlesung gewählt!");
                            noSelectedLecture.setMessage("Bitte wähle in deinem Vorlesungsmenü eine Vorlesung aus!");
                            noSelectedLecture.setCancelable(true);
                            noSelectedLecture.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            noSelectedLecture.create().show();
                        } else {
                            Intent gap_question = new Intent(ChooseModeActivity.this, Add_Gap_Question_Activity.class);
                            startActivity(gap_question);
                            killBackground();
                            mMediaPlayer.stop();
                            break;
                        }
                    }
                    case 1: {
                        // If no lecture is selected
                        if (lid == -1) {
                            AlertDialog.Builder noSelectedLecture = new AlertDialog.Builder(ChooseModeActivity.this);
                            noSelectedLecture.setTitle("Keine Vorlesung gewählt!");
                            noSelectedLecture.setMessage("Bitte wähle in deinem Vorlesungsmenü eine Vorlesung aus!");
                            noSelectedLecture.setCancelable(true);
                            noSelectedLecture.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            noSelectedLecture.create().show();
                        } else {
                            Intent normal_question = new Intent(ChooseModeActivity.this, Add_Normal_Question_Activity.class);
                            startActivity(normal_question);
                            killBackground();
                            mMediaPlayer.stop();
                            break;
                        }
                    }
                    default: {
                        break;
                    }
                }
            }
        });

        // Add ClickListener to 'exam'-Button to go to play mode
        examMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If no lecture is selected
                if (lid == -1) {
                    AlertDialog.Builder noSelectedLecture = new AlertDialog.Builder(ChooseModeActivity.this);
                    noSelectedLecture.setTitle("Keine Vorlesung gewählt!");
                    noSelectedLecture.setMessage("Bitte wähle in deinem Vorlesungsmenü eine Vorlesung aus!");
                    noSelectedLecture.setCancelable(true);
                    noSelectedLecture.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    noSelectedLecture.create().show();
                } else {
                    questionContent = new ArrayList<>();
                    questionType = new ArrayList<>();
                    questionID = new ArrayList<>();
                    // Make download request for questions. The result is saved into the given ArrayLists
                    DatabaseController db = new DatabaseController(DBVars.REQUEST_QUESTION_DOWNLOAD,
                            "http://android.getenv.net/?mod=Lecture&fun=getQuestions&lid=" + lid,
                            questionContent, questionType, questionID);
                    db.start();
                    while (db.isAlive()) ;
                    if (questionContent.get(0).equals("No-Question")) {
                        AlertDialog.Builder noQuestions = new AlertDialog.Builder(ChooseModeActivity.this);
                        noQuestions.setTitle("Für diese Vorlesungen existieren keine Fragen!");
                        noQuestions.setMessage("Erstelle als erster eine Frage für diese Vorlesung!");
                        noQuestions.setCancelable(true);
                        noQuestions.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        noQuestions.create().show();
                    } else {
                        Intent startPlay = new Intent(ChooseModeActivity.this, QuestionModePigActivity.class);
                        Bundle extras = new Bundle();
                        extras.putStringArrayList("Content", questionContent);
                        extras.putIntegerArrayList("Type", questionType);
                        extras.putIntegerArrayList("Question-ID", questionID);
                        startPlay.putExtras(extras);
                        startActivity(startPlay);
                        killBackground();
                        mMediaPlayer.stop();
                    }
                }
            }
        });

    }

    private void colorizeSelected() {
        SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        int pos = prefs.getInt("Selected-Lecture", -1);
        if(pos != -1) {
            // Set color to selected lecture
            View view = lecture_menu.getChildAt(pos);
            TextView line = (TextView) view.findViewById(R.id.menu_item);
            line.setTextColor(Color.parseColor("#FF8FEBFF"));
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void updateDrawer() {
        lectures.add("Logout");
        lectures.add("Hinzufügen...");
        lectures.add("Suchen...");

        // Make download request for lectures. The result is saved into the given ArrayLists
        DatabaseController db = new DatabaseController(DBVars.REQUEST_LECTURES_DOWNLOAD,
                "http://android.getenv.net/?mod=User&fun=getLectures&uid=" + uid, lectures, lectureID);
        db.start();
        while (db.isAlive()) ;
        lectures.add("Zurücksetzen");
    }

    // Restart of the sound of birds
    @Override
    public void onRestart() {
        super.onRestart();
        mMediaPlayer = MediaPlayer.create(this, R.raw.bird);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    // Stop bird sound when user logs out
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
       /* SharedPreferences prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("User-ID");
        editor.remove("Lecture-ID");
        editor.remove("Selected-Lecture");
        editor.apply(); */
    }

    private void killBackground(){
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
        }
        killBackground();
    }

}
