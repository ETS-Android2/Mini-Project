package com.jagadeesh.smartfarming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private String userId;

    public static String RwriteAPIkey, RreadAPIkey, RchannelId, RminTemp, RmaxTemp;

    public static TextView tempView, humidityView;

    Button startBtn, stopBtn, setTempBtn, updateApiBtn;

    public static ProgressBar progressBarHomePage;

    public static boolean isActive, tempLimitInstance;

    int currentTemp, minTempLimit = 0, maxTempLimit = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        progressBarHomePage = findViewById(R.id.progressbar_homepage);

        updateApiBtn = findViewById(R.id.update_api_btn_home);
        updateApiBtn.setOnClickListener(this);

        tempView = findViewById(R.id.temperature_view);
        humidityView = findViewById(R.id.humidity_view);

        setTempBtn = findViewById(R.id.set_temp_value_btn);
        setTempBtn.setOnClickListener(this);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        UpdatedFirebaseData();

        startBtn = findViewById(R.id.start_btn_homepage);
        startBtn.setOnClickListener(this);

        stopBtn = findViewById(R.id.stop_btn_homepage);
        stopBtn.setOnClickListener(this);
    }

    private void Content() {

        progressBarHomePage.setVisibility(View.VISIBLE);
        UpdatedFirebaseData();
        new fetchDataThingSpeak().execute();

        if (!SendDataThingSpeak.tempLimitEx) {

            System.out.println("Temperature Limit State got Called");
            ReadTempLimitState();

        } else {
            tempLimitInstance = true;
        }

        if (tempLimitInstance) {
            NotifyNotification();
        }

        if (isActive) {

            Refresh(5000);

        }

    }

    private void Refresh(int _millieSeconds) {

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Content();
            }
        };

        handler.postDelayed(runnable, _millieSeconds);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about_menu:
                startActivity(new Intent(HomePage.this, About.class));
                return true;

            case R.id.logout_menu:

                SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomePage.this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_btn_homepage:
                isActive = true;
                Content();
                break;

            case R.id.stop_btn_homepage:
                isActive = false;
                break;

            case R.id.set_temp_value_btn:
                startActivity(new Intent(HomePage.this, SetTemperatureValue.class));
                break;

            case R.id.update_api_btn_home:
                startActivity(new Intent(HomePage.this, UpdateApiKeys.class));
                break;

        }

    }

    public void UpdatedFirebaseData() {

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userProfile = snapshot.getValue(UserData.class);

                if (userProfile != null) {
                    RwriteAPIkey = userProfile.writeAPIkey;
                    RreadAPIkey = userProfile.readAPIkey;
                    RchannelId = userProfile.channelId;
                    RminTemp = userProfile.minTemp;
                    RmaxTemp = userProfile.maxTemp;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(HomePage.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void NotifyNotification() {

        currentTemp = fetchDataThingSpeak.nonZeroCurrentTempValue;

        if (minTempLimit == 0 || maxTempLimit == 0) {

            minTempLimit = Integer.parseInt(RminTemp);
            maxTempLimit = Integer.parseInt(RmaxTemp);

        } else if (SendDataThingSpeak.tempLimitEx) {

            minTempLimit = Integer.parseInt(SendDataThingSpeak.minTempInput);
            maxTempLimit = Integer.parseInt(SendDataThingSpeak.maxTempInput);

        }

        if (currentTemp > maxTempLimit && currentTemp != 0) {

            String message = "Temperature has Exceeded the Maximum Limit";
            Notification(message);

        } else if (currentTemp < minTempLimit && currentTemp != 0) {

            String message = "Temperature is Below Minimum Limit";
            Notification(message);

        }


    }


    private void Notification(String _message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("not1", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(HomePage.this, "not1")
                .setContentTitle("Smart Farming")
                .setSmallIcon(R.drawable.ic_smart_notification)
                .setAutoCancel(true)
                .setContentText(_message);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(55, notificationBuilder.build());

    }

    private void ReadTempLimitState() {

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userProfile = snapshot.getValue(UserData.class);

                if (userProfile != null) {
                    tempLimitInstance = userProfile.TempLimitState;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(HomePage.this, "Unable to fetch temp limits! Try again", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {

        this.finishAffinity();

        super.onBackPressed();
    }
}


