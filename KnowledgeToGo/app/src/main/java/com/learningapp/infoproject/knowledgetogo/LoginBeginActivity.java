package com.learningapp.infoproject.knowledgetogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Script;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginBeginActivity extends Activity {

    private EditText name;
    private EditText password;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_begin);

        // Import layout features
        name = (EditText) findViewById(R.id.login_name);
        password = (EditText) findViewById(R.id.enterPassword);
        Button login = (Button) findViewById(R.id.login);
        TextView createAccount = (TextView) findViewById(R.id.newAccount);
        ImageView pig = (ImageView) findViewById(R.id.schwein_steht);
        pig.setImageResource(R.drawable.schwein_steht);


        // Set ClickListener on "register"-Textview
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Register-Activity
                Intent register = new Intent(LoginBeginActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
            }
        });

        // Set ClickListener on "login"-Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get inserted information from login
                userName = name.getText().toString();
                userPassword = password.getText().toString();

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo internetOk = connectivityManager.getActiveNetworkInfo();
                if(internetOk != null) {
                    // Creates NetworkController and Handler to send request to server
                    new NetworkController(userName, "userNameAvailable&name=", LoginHandler).start();
                } else {
                    AlertDialog.Builder noInternet = new AlertDialog.Builder(LoginBeginActivity.this);
                    noInternet.setTitle(R.string.no_internet);
                    noInternet.setMessage(R.string.no_internet2);
                    noInternet.setCancelable(true);
                    noInternet.create().show();
                    password.getText().clear();
                    name.getText().clear();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {

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

    private class NetworkController extends Thread {
        private String name;
        private String function;
        private Handler resultHandler;

        public NetworkController (String name, String function, Handler resultHandler) {
            this.name = name;
            this.function = function;
            this. resultHandler = resultHandler;
        }

        @Override
        public void run() {
            URL request = null;
            try {
                request = new URL("http://android.getenv.net/?mod=User&fun=" + function + name);

                HttpURLConnection sendInfo = (HttpURLConnection) request.openConnection();
                // Send session-cookie in header
                if (DBVars.SESSION_COOKIE != null){
                    sendInfo.setRequestProperty("Cookie", DBVars.SESSION_COOKIE);
                    sendInfo.connect();
                }

                BufferedReader getData = new BufferedReader(new InputStreamReader(sendInfo.getInputStream()));

                // Put 'endless' line of data into a readable portion
                StringBuffer responseData = new StringBuffer(1024);
                String tmp = "";
                while((tmp = getData.readLine()) != null) {
                    responseData.append(tmp);
                }
                getData.close();

                // Get session cookie to stay logged in
                if (DBVars.SESSION_COOKIE == null) {
                    String headerNameTMP = null;
                    Object cookieValue;
                    for (int i = 1; (headerNameTMP = sendInfo.getHeaderFieldKey(i)) != null; i++) {
                        if (headerNameTMP.equals("Set-Cookie")) {
                            cookieValue = sendInfo.getHeaderField(i);
                            cookieValue.getClass();
                            DBVars.SESSION_COOKIE = cookieValue.toString();
                        }
                    }
                }

                // Set up message for handler with the result-data
                Message responseOfWeb = resultHandler.obtainMessage();
                Bundle resultBundle = new Bundle();
                responseOfWeb.setData(resultBundle);
                resultBundle.putString("Login", responseData.toString());
                resultHandler.sendMessage(responseOfWeb);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String pass;
    String infoFromServer;

    //sets up new handler
    private final Handler LoginHandler = new Handler() {

        public void handleMessage(Message responseServerMessage) {

            //gets the message with info from handler (bundle) if userName is already used
            Bundle data = responseServerMessage.getData();

            try {
                // Get Info from message
                infoFromServer = data.getString("Login");
                try {
                    // User-name is not available
                    if (Integer.parseInt(infoFromServer) == 0) {
                        Toast.makeText(LoginBeginActivity.this, R.string.no_user, Toast.LENGTH_LONG).show();
                        password.getText().clear();
                        name.getText().clear();
                    } else {
                        // User-name is available, next request to compare password
                        new NetworkController(userName, "getPass&name=", LoginHandler).start();
                    }
                } catch (Exception e) {
                    // Get correct password from database
                    JSONObject passWrapped = new JSONObject(infoFromServer);
                    pass = passWrapped.getString("UPass");

                    // Entered password is not the same as in database
                    if (!pass.equals(userPassword)) {
                        Toast.makeText(LoginBeginActivity.this, R.string.wrong_password, Toast.LENGTH_LONG).show();
                        password.getText().clear();
                        // If entered password is correct, go on to ChooseModeActivity
                    } else {
                        // Keks
                        Toast.makeText(LoginBeginActivity.this, DBVars.SESSION_COOKIE , Toast.LENGTH_LONG).show();

                        Intent chooseMode = new Intent(LoginBeginActivity.this, ChooseModeActivity.class);
                        startActivity(chooseMode);
                        finish();
                    }
                }
            } catch (Exception e) {
            }
        }
    };
}
