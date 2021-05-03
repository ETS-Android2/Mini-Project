package com.jagadeesh.smartfarming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class UpdateApiKeys extends AppCompatActivity implements View.OnClickListener {

    EditText channelId, writeApi, readApi;
    Button channelIdBtn, writeApiBtn, readApiBtn;

    public static TextView channelIdTextView, writeApiTextView, readApiTextView;

    private String channelIdPreValue, writeApiPreValue, readApiPreValue;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_api_keys);

        channelId = findViewById(R.id.channelid_update_api_keys);
        writeApi = findViewById(R.id.write_api_update_api_keys);
        readApi = findViewById(R.id.read_api_update_api_keys);

        channelIdTextView = findViewById(R.id.channelid_view_updateapi);
        writeApiTextView = findViewById(R.id.writeapi_view_updateapi);
        readApiTextView = findViewById(R.id.readapi_view_updateapi);

        progressBar = findViewById(R.id.progressbar_update_api);

        channelIdBtn = findViewById(R.id.channelid_btn_update_api_key);
        channelIdBtn.setOnClickListener(this);

        writeApiBtn = findViewById(R.id.write_api_btn_update_api_key);
        writeApiBtn.setOnClickListener(this);


        readApiBtn = findViewById(R.id.read_api_btn_update_api_key);
        readApiBtn.setOnClickListener(this);

        GetApis();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.channelid_btn_update_api_key:

                String channelFirebaseChildName = "channelId";
                EditText channelEditTextViewName = channelId;
                String channelIdInput = channelId.getText().toString().trim();

                Updater(channelIdInput, channelFirebaseChildName, channelEditTextViewName, channelIdTextView);
                break;

            case R.id.write_api_btn_update_api_key:

                String writeFirebaseChildName = "writeAPIkey";
                EditText writeEditTextViewName = writeApi;
                String writeApiInput = writeApi.getText().toString().trim();

                Updater(writeApiInput, writeFirebaseChildName, writeEditTextViewName, writeApiTextView);
                break;

            case R.id.read_api_btn_update_api_key:

                String readFirebaseChildName = "readAPIkey";
                EditText readEditTextViewName = readApi;
                String readApiInput = readApi.getText().toString().trim();

                Updater(readApiInput, readFirebaseChildName, readEditTextViewName, readApiTextView);
                break;

        }

    }

    private void Updater(String _inputValue, String _firebaseChildName, EditText _editTextViewName, TextView _textView) {

        if (_inputValue.isEmpty()) {
            _editTextViewName.setError("This Field Cannot be Empty");
            _editTextViewName.requestFocus();
            return;
        }

        AlertDialog.Builder updateApiDialog = new AlertDialog.Builder(UpdateApiKeys.this);

        updateApiDialog.setTitle("Update?")
                .setMessage("Are You Sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressBar.setVisibility(View.VISIBLE);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(_firebaseChildName)
                                .setValue(_inputValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(UpdateApiKeys.this, "Successfully Updated", Toast.LENGTH_LONG).show();
                                    _editTextViewName.setText("");
                                    _textView.setText(_inputValue);

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(UpdateApiKeys.this, "Couldn't Update! Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog updateApiAlert = updateApiDialog.create();
        updateApiAlert.show();
    }

    private void GetApis() {

        DatabaseReference databaseReference;
        String userId;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserData userProfile = snapshot.getValue(UserData.class);

                if (userProfile != null) {

                    channelIdPreValue = userProfile.channelId;
                    writeApiPreValue = userProfile.writeAPIkey;
                    readApiPreValue = userProfile.readAPIkey;

                    channelIdTextView.setText(channelIdPreValue);
                    writeApiTextView.setText(writeApiPreValue);
                    readApiTextView.setText(readApiPreValue);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateApiKeys.this, "Unable to Fetch the Api keys! try again!", Toast.LENGTH_SHORT).show();

            }
        });


    }
}