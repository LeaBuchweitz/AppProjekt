package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ChooseModeActivity extends Activity {

    // In inner class needed objects
    private ListView addList;
    private DrawerLayout drawer;
    private ListView lecture_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        // Import layout feature
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        Button addQuestion = (Button) findViewById(R.id.add_question);
        addList = (ListView) findViewById(R.id.add_list);
        Button examMode = (Button) findViewById(R.id.exam);
        ImageButton chooseLecture = (ImageButton) findViewById(R.id.choose_lecture);
        chooseLecture.setImageResource(R.drawable.doktor_hut);
        lecture_menu = (ListView) findViewById(R.id.lecture_menu);

        // Open Drawer-Menu by clicking on Button
        chooseLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        // Set Drawer-Layout to choose different lectures
        final ArrayList<String> lectures = new ArrayList<String>();
        lectures.add("Hinzufügen...");
        lectures.add("Logout");
        lecture_menu.setAdapter(new ArrayAdapter<String>(this, R.layout.one_lecture_line, lectures));

        // Add ClickListener to enter a special lecture
        lecture_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLecture = (String) lecture_menu.getItemAtPosition(position);

                // ToDo load questions of this lecture
                if(selectedLecture.equals("Hinzufügen...")) {
                    final EditText enterLecture = new EditText(ChooseModeActivity.this);
                    AlertDialog.Builder newLecture = new AlertDialog.Builder(ChooseModeActivity.this);
                    newLecture.setMessage("Gib den Namen der Vorlesung ein!");
                    newLecture.setView(enterLecture);
                    newLecture.setCancelable(true);
                    newLecture.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String addThisLecture = enterLecture.getText().toString();
                            //ToDo give new lecture to server and save it
                        }
                    });
                    newLecture.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    newLecture.create().show();
                } else if (selectedLecture.equals("Logout")) {
                    // ToDo logout
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
                if(addList.getVisibility() == 0) {
                    addList.setVisibility(View.INVISIBLE);
                } else {
                    addList.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_mode, menu);
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
