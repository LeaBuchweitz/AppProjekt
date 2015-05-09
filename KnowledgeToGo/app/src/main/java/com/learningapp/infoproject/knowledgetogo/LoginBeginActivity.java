package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginBeginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_begin);

        // Import layout features
        EditText name = (EditText) findViewById(R.id.login_name);
        EditText password = (EditText) findViewById(R.id.enterPassword);
        Button login = (Button) findViewById(R.id.login);
        TextView createAccount = (TextView) findViewById(R.id.newAccount);

        // Set ClickListener on "register"-Textview
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Register-Activity
                Intent register = new Intent(LoginBeginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });

        // Get inserted information from login
        String userName = name.getText().toString();
        String userPassword = password.getText().toString();

        // ToDo Compare entered information to information on server

        // Set ClickListener on "login"-Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to chooseMode-Activity
                Intent chooseMode = new Intent(LoginBeginActivity.this, ChooseModeActivity.class);
                startActivity(chooseMode);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_begin, menu);
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
