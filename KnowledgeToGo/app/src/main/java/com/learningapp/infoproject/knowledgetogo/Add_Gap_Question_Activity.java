package com.learningapp.infoproject.knowledgetogo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Add_Gap_Question_Activity extends ActionBarActivity {

    private EditText content;
    private int lid;
    private int uid;
    private boolean uploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__gap__question_);
        content = (EditText) findViewById(R.id.text);
        uploaded = false;
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
}
