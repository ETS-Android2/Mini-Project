package com.jagadeesh.smartfarming;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SendDataThingSpeak extends AsyncTask<String, String, String> {

    public static String writeAPIkey;
    public static String minTempInput, maxTempInput;

    public static boolean tempLimitEx;

    @Override
    protected void onPreExecute() {

        writeAPIkey = HomePage.RwriteAPIkey;

        minTempInput = SetTemperatureValue.minTemp.getText().toString().trim();
        maxTempInput = SetTemperatureValue.maxTemp.getText().toString().trim();

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL url1 = new URL("https://api.thingspeak.com/update?api_key=" + writeAPIkey + "&field3=" + minTempInput + "&field4=" + maxTempInput);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url1).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    SetTemperatureValue.progressBarSetTemp.setVisibility(View.INVISIBLE);
                    SetTemperatureValue.statusResult.setText("Something Went Wrong");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        SetTemperatureValue.progressBarSetTemp.setVisibility(View.INVISIBLE);
                        SetTemperatureValue.statusResult.setText("Updated Succesfully");
                        tempLimitEx = true;

                    }

                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }
}
