package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends Activity {

    // In code often used layout features
    EditText name;
    EditText password;
    EditText checkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Import layout features
        Button register = (Button) findViewById(R.id.register);
        name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        checkPassword = (EditText) findViewById(R.id.verify_password);

        // Check if password is the same and if user name is already used
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String passwordFirst = password.getText().toString();
                String passwordSecond = checkPassword.getText().toString();
                // ToDo check if user name is already used
                if (userName.equals("")|| passwordFirst.equals("") || passwordSecond.equals("")) {
                    AlertDialog.Builder notValid = new AlertDialog.Builder(RegisterActivity.this);
                    notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    notValid.setMessage("Bitte fülle alle Felder aus!");
                    notValid.create().show();
                } else if (!(passwordFirst.equals(passwordSecond))) {
                    AlertDialog.Builder notValid = new AlertDialog.Builder(RegisterActivity.this);
                    notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    notValid.setMessage("Die Passwörter stimmen nicht überein! Versuch' es noch einmal!");
                    notValid.create().show();
                } else {
                    //ToDo give password to server
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
