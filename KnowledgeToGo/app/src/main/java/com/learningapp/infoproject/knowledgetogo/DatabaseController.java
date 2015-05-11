package com.learningapp.infoproject.knowledgetogo;

import android.util.Log;

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

    public DatabaseController(int requestType, String url, ArrayList<String> content, ArrayList<Integer> type, ArrayList<Integer> id){
        this.url = url;
        this.content = content;
        this.type = type;
        this.id = id;
        this.requestType = requestType;
    }

    @Override
    public void run() {
        URL request;

        try {
            BufferedReader getData;
            request = new URL(url);

            HttpURLConnection sendInfo = (HttpURLConnection) request.openConnection();
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
                }

            } catch (JSONException e) {
                Log.i("a",e.toString());
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}