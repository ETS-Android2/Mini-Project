package com.jagadeesh.smartfarming;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.health.SystemHealthManager;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;

public class fetchDataThingSpeak extends AsyncTask<String, String, String> {

    String parsedTempData = "";
    String parsedHumidityData = "";

    public static int nonZeroCurrentTempValue = 0;


    public static String readAPIkey, channelId;

    @Override
    protected String doInBackground(String... strings) {

        readAPIkey = HomePage.RreadAPIkey;
        channelId = HomePage.RchannelId;

        try {
            URL url1 = new URL("https://api.thingspeak.com/channels/" + channelId + "/fields/1.json?api_key=" + readAPIkey + "&results=2");
            URL url2 = new URL("https://api.thingspeak.com/channels/" + channelId + "/fields/2.json?api_key=" + readAPIkey + "&results=2");

            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
            InputStream inputStream1 = connection1.getInputStream();
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1));

            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
            InputStream inputStream2 = connection2.getInputStream();
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));


            String line1, line2;

            line1 = bufferedReader1.readLine();
            line2 = bufferedReader2.readLine();

            JSONObject jsnobject1 = new JSONObject(line1);
            JSONObject jsnobject2 = new JSONObject(line2);

            JSONArray jsonArray1 = jsnobject1.getJSONArray("feeds");
            JSONArray jsonArray2 = jsnobject2.getJSONArray("feeds");

            JSONObject jsonObject3 = (JSONObject) jsonArray1.get(1);
            JSONObject jsonObject4 = (JSONObject) jsonArray2.get(1);


            parsedTempData = jsonObject3.get("field1").toString();
            parsedHumidityData = jsonObject4.get("field2").toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        System.out.println("First Temp Data :" + parsedTempData + "First Humidity Data: " + parsedHumidityData);

        if (!parsedTempData.isEmpty() && !parsedTempData.equals("null")) {

            int parsedTempDataInt = Math.round(Float.parseFloat(parsedTempData));

            HomePage.tempView.setText(String.valueOf(parsedTempDataInt));

            nonZeroCurrentTempValue = parsedTempDataInt;

        }

        if (!parsedHumidityData.isEmpty() && !parsedHumidityData.equals("null")) {

            HomePage.humidityView.setText(parsedHumidityData);

        }

        HomePage.progressBarHomePage.setVisibility(View.INVISIBLE);

        super.onPostExecute(s);
    }

}
