package com.learningapp.infoproject.knowledgetogo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class Add_Gap_Question_Activity extends ActionBarActivity {

    private EditText content;
    private int lid;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__gap__question_);
        content = (EditText) findViewById(R.id.question);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add__gap__question_, menu);
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

    public void sendQuestion(View view){
        String upload = content.getText().toString();
        if(Parser.checkParenthesis(upload)) {
            new DatabaseController(DBVars.REQUEST_QUESTION_INSERT,
                    "http://android.getenv.net/?mod=Lecture&fun=insertQuestions&qType=" +
                            DBVars.QUESTION_TYPE_GAPTEXT +
                            "&lid=" + lid + "&uid=" + uid + "&content=" + upload).start();
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
