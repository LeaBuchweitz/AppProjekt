package com.learningapp.infoproject.knowledgetogo;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Fabian on 10.05.15.
 */
public class DatabaseController extends Thread {

    private String url;
    private ArrayList<String> content;
    private ArrayList<Integer> type;
    private ArrayList<Integer> id;
    private int requestType;
    private String lecture;
    private int userID;

    public DatabaseController(int requestType, String url, ArrayList<String> content, ArrayList<Integer> type, ArrayList<Integer> id){
        this.url = url;
        this.content = content;
        this.type = type;
        this.id = id;
        this.requestType = requestType;
    }

    public DatabaseController(int requestType, String url, ArrayList<String> string, ArrayList<Integer> integer){
        this.url = url;
        this.content = string;
        this.type = integer;
        this.requestType = requestType;
    }


    public DatabaseController (int requestType, String url, String lecture, int userID) {
        this.userID = userID;
        this.lecture = lecture;
        this.url = url;
        this.requestType = requestType;
    }

    public DatabaseController (int requestType, String url, String lecture) {
        this.lecture = lecture;
        this.url = url;
        this.requestType = requestType;
    }

    public DatabaseController (int requestType, String url) {
        this.url = url;
        this.requestType = requestType;
    }

    @Override
    public void run() {
        URL request;

        try {
            BufferedReader getData;
            request = new URL(url);

            HttpURLConnection sendInfo = (HttpURLConnection) request.openConnection();
            sendInfo.setRequestProperty("Cookie", DBVars.SESSION_COOKIE);
            sendInfo.connect();
            getData = new BufferedReader(new InputStreamReader(sendInfo.getInputStream()));

            // Put 'endless' line of data into a readable portion
            StringBuilder responseData = new StringBuilder(1024);
            String tmp = "";
            while((tmp = getData.readLine()) != null) {
                responseData.append(tmp);
            }
            getData.close();


            try {
                JSONObject jO;
                JSONArray download = new JSONArray(new String(responseData));

                switch (requestType) {
                    case DBVars.REQUEST_QUESTION_DOWNLOAD:
                        for (int i = 0; i < download.length(); i++) {
                            jO = download.getJSONObject(i);
                            content.add(jO.getString("QContent"));
                            type.add(jO.getInt("QType"));
                            id.add(jO.getInt("QID"));
                        }
                        break;
                    case DBVars.REQUEST_BEST_USER:
                        for (int i = 0; i < download.length(); i++) {
                            jO = download.getJSONObject(i);
                            content.add(jO.getString("UName"));
                            type.add(jO.getInt("SScore"));
                        }
                        break;
                    case DBVars.REQUEST_LECTURES_DOWNLOAD:
                        for (int i = 0; i < download.length(); i++) {
                            jO = download.getJSONObject(i);
                            content.add(jO.getString("LName"));
                            type.add(jO.getInt("LID"));
                        }
                        break;
                    case DBVars.REQUEST_NEW_LECTURE:
                        String success = null;
                        for (int i = 0; i < download.length(); i++) {
                            jO = download.getJSONObject(i);
                            success = jO.getString("LName");
                        }
                        if(success.equals(lecture)) {
                            Log.i("lecture", "Vorlesung hochgeladen");
                        }
                        break;
                    case DBVars.REQUEST_ALREADY_THERE:
                        if(new String (responseData).equals("[null]")) {
                            new DatabaseController(DBVars.REQUEST_NEW_LECTURE,
                                    "http://android.getenv.net/?mod=Lecture&fun=insertLecture&name="+lecture+"&uid="+userID, lecture).start();
                        } else {
                            Log.i("lecture", "Vorlesung gibts schon!");
                        }
                        break;
                }

            } catch (JSONException e) {
                Log.i("a", e.toString());
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}