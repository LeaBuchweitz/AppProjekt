package com.learningapp.infoproject.knowledgetogo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegisterActivity extends Activity {

    // In code often used layout features
    private EditText name;
    private EditText password;
    private EditText checkPassword;
    protected boolean userNameAvailable;
    private boolean requestSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Import layout features
        final Button register = (Button) findViewById(R.id.register);
        name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        checkPassword = (EditText) findViewById(R.id.verify_password);

        requestSent = false;

        // Check if password is the same and if user name is already used
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestSent) {
                    String userName = name.getText().toString();
                    String passwordFirst = password.getText().toString();
                    String passwordSecond = checkPassword.getText().toString();
                    if (userName.equals("") || passwordFirst.equals("") || passwordSecond.equals("")) {
                        AlertDialog.Builder notValid = new AlertDialog.Builder(RegisterActivity.this);
                        notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        notValid.setMessage("Bitte fülle alle Felder aus!");
                        notValid.create().show();
                        return;
                    }
                    if (!(passwordFirst.equals(passwordSecond))) {
                        AlertDialog.Builder notValid = new AlertDialog.Builder(RegisterActivity.this);
                        notValid.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        notValid.setMessage("Die Passwörter stimmen nicht überein! Versuch' es noch einmal!");
                        notValid.create().show();
                        return;
                    }
                    // Check if username is already used
                    if (userName != null) {
                        // Give Info to NetworkController and Handler
                        sendRequest(RegisterActivity.this, userName, null, false);
                    }
                    // If request was sent you can't press the button
                    if (userNameAvailable) {
                        requestSent = true;
                    }
                }
            }
        });
    }

    //sending data to the webpage and get current weather data back
    public void sendRequest(Context context, String userName, String password, boolean insert) {
        //creating NetworkController
        new NetworkController(context, userName, password, registerHandler, insert).start();
    }

    //sending data to the webpage and get current weather data back
    public void sendInsertRequest() {
        //creating NetworkController
        new NetworkController(this, name.getText().toString(), password.getText().toString(), registerHandler, true).start();
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


    //handles the request in another thread and gives possibility to get data back with handler
    private class NetworkController extends Thread {
        private String userName;
        private Context context;
        private Handler resultHandler;
        private String password;
        private boolean insert;

        public NetworkController(Context context, String userName, String password, Handler resultHandler, boolean insert) {
            this.userName = userName;
            this.password =  password;
            this.context = context;
            this.resultHandler = resultHandler;
            this.insert = insert;
        }

        //send request to webpage and give message to handler to handle response of webpage
        @Override
        public void run() {
            URL request;
            try {
                BufferedReader getData;
                if(!insert) {
                    request = new URL("http://android.getenv.net/?mod=User&fun=userNameAvailable&name=" + userName);
                } else {
                    request = new URL("http://android.getenv.net/?mod=User&fun=add&name=" + userName + "&pass=" + password);
                }
                HttpURLConnection sendInfo = (HttpURLConnection) request.openConnection();
                getData = new BufferedReader(new InputStreamReader(sendInfo.getInputStream()));

                // Put 'endless' line of data into a readable portion
                StringBuffer responseData = new StringBuffer(1024);
                String tmp = "";
                while((tmp = getData.readLine()) != null) {
                    responseData.append(tmp);
                }
                getData.close();

                // Set up message for handler with the result-data
                Message responseOfWeb = resultHandler.obtainMessage();
                Bundle resultBundle = new Bundle();
                responseOfWeb.setData(resultBundle);
                // Label response of the webpage with 'Available' or 'Insert'
                if(!insert) {
                    resultBundle.putString("Available", responseData.toString());
                } else {
                    resultBundle.putString("Insert", responseData.toString());
                }
                resultHandler.sendMessage(responseOfWeb);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //sets up new handler
    private final Handler registerHandler = new Handler() {

        public void handleMessage(Message responseServerMessage) {
            //gets the message with info from handler (bundle) if userName is already used
            Bundle data = responseServerMessage.getData();

            // Get Info from message
            try {
                String infoFromServer = data.getString("Available");
                if(Integer.parseInt(infoFromServer) > 0) {
                    Toast.makeText(RegisterActivity.this, "Der Username existiert schon! Nimm einen Anderen!", Toast.LENGTH_LONG).show();
                    userNameAvailable = false;
                } else {
                    Toast.makeText(RegisterActivity.this, "Bitte warten...", Toast.LENGTH_LONG).show();
                    userNameAvailable = true;
                    sendInsertRequest();
                }
            } catch (Exception e) {
            }

            try {
                String infoFromServer = data.getString("Insert");
                if(infoFromServer.equals("true")) {
                    Toast.makeText(RegisterActivity.this, "Du bist registriert! Du kannst dich jetzt einloggen!", Toast.LENGTH_LONG).show();
                    requestSent = false;
                }
            } catch (Exception e) {
            }


        }
    };
}
