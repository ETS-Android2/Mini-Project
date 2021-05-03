package com.jagadeesh.smartfarming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetTemperatureValue extends AppCompatActivity implements View.OnClickListener {

    public static EditText minTemp, maxTemp;
    Button updateBtn;

    public static TextView statusResult, minTempView, maxTempView;

    public static ProgressBar progressBarSetTemp;

    public static String writeAPIkey, _minTempInput, _maxTempInput;

    private static String RminTemp, RmaxTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_temperature_value);

        progressBarSetTemp = findViewById(R.id.progressbar_settemp);

        statusResult = findViewById(R.id.status_result_temp);

        writeAPIkey = HomePage.RwriteAPIkey;

        minTempView = findViewById(R.id.min_temp_view);
        maxTempView = findViewById(R.id.max_temp_view);

        minTemp = findViewById(R.id.min_temp_value);
        maxTemp = findViewById(R.id.max_temp_value);

        updateBtn = findViewById(R.id.update_btn_settemp);
        updateBtn.setOnClickListener(this);

        ReadTempLimitsFirebase();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_btn_settemp:

                Update();

                break;
        }

    }

    private void Update() {

        _minTempInput = minTemp.getText().toString().trim();
        _maxTempInput = maxTemp.getText().toString().trim();

        if (_minTempInput.isEmpty()) {
            minTemp.setError("This Field is Required!");
            minTemp.requestFocus();
            return;
        }

        if (_maxTempInput.isEmpty()) {
            maxTemp.setError("This Field is Required!");
            maxTemp.requestFocus();
            return;
        }

        if (Integer.parseInt(_minTempInput) > Integer.parseInt(_maxTempInput)) {
            minTemp.setError("Minimum Temperature Value is Greater Than Maximum Temperature");
            minTemp.requestFocus();
            return;

        } else {
            new SendDataThingSpeak().execute();

            SetTempLimitState("TempLimitState", true);

            UpdateTempLimitsFirebase(minTempView, "minTemp", _minTempInput);
            UpdateTempLimitsFirebase(maxTempView, "maxTemp", _maxTempInput);

            progressBarSetTemp.setVisibility(View.VISIBLE);
        }
    }


    private void UpdateTempLimitsFirebase(TextView _textViewName, String _firebaseChildName, String _tempValue) {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(_firebaseChildName)
                .setValue(_tempValue).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    _textViewName.setText(_tempValue);
                    Toast.makeText(SetTemperatureValue.this, "Successfully Updated", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(SetTemperatureValue.this, "Couldn't Update! Something Went Wrong", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void SetTempLimitState(String _firebaseChildName, boolean _value) {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(_firebaseChildName)
                .setValue(_value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(SetTemperatureValue.this, "Successfully Updated", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(SetTemperatureValue.this, "Couldn't Update! Something Went Wrong", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void ReadTempLimitsFirebase() {

        DatabaseReference databaseReference;
        String userId;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userProfile = snapshot.getValue(UserData.class);

                if (userProfile != null) {

                    RminTemp = userProfile.minTemp;
                    RmaxTemp = userProfile.maxTemp;
                }

                if (!RminTemp.isEmpty() || !RminTemp.equals("null") && !RmaxTemp.isEmpty() || !RmaxTemp.equals("null")) {

                    minTempView.setText(RminTemp);
                    maxTempView.setText(RmaxTemp);

                } else {

                    String msg = "Unable to set";

                    minTempView.setText(msg);
                    maxTempView.setText(msg);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}