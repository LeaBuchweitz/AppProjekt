package com.learningapp.infoproject.knowledgetogo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Add_Gap_Question_Activity extends ActionBarActivity {

    private EditText content;
    private int lid;
    private int uid;
    private boolean uploaded;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__gap__question_);
        content = (EditText) findViewById(R.id.text);
        uploaded = false;

        // Get Info from SharedPreferences for User-ID
        prefs = getSharedPreferences("com.learningapp.infoproject.knowledgetogo", Context.MODE_PRIVATE);
        uid = prefs.getInt("User-ID",0);
        lid = prefs.getInt("Lecture-ID",0);

        // DialogBox with instructions how to mark a gap
        if(prefs.getBoolean("Instruction",true)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_gap_question, null);
            dialog.setCancelable(true);
            TextView text = (TextView) view.findViewById(R.id.instruction);
            text.setGravity(Gravity.CENTER);
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
    }

    public void sendQuestion(View view){
        String upload = content.getText().toString();

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

        if (Parser.checkParenthesis(upload)) {
            uploaded = true;
            DatabaseController db = new DatabaseController(DBVars.REQUEST_QUESTION_INSERT,
                    "http://android.getenv.net/?mod=Lecture&fun=insertQuestion&type=" +
                            DBVars.QUESTION_TYPE_GAPTEXT +
                            "&lid=" + lid + "&uid=" + uid + "&content=" + upload);
            db.start();
            while(db.isAlive());
            Toast.makeText(this, R.string.Upload_succesfull, Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder notValid = new AlertDialog.Builder(this);
            notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            notValid.setMessage(R.string.gapTextNotValid);
            notValid.create().show();
            return;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(Add_Gap_Question_Activity.this, ChooseModeActivity.class);
        startActivity(intent);
        finish();
    }
}
