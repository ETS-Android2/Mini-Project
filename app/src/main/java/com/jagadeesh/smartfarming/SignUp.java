package com.jagadeesh.smartfarming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    String minTemp = "", maxTemp = "";

    EditText fullName, email, password, writeAPIkey, readAPIkey, channelId;
    Button signUpBtn;
    TextView loginHereSignUp;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        channelId = findViewById(R.id.channelid_signup);
        fullName = findViewById(R.id.fullname_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        writeAPIkey = findViewById(R.id.write_api_signup);
        readAPIkey = findViewById(R.id.read_api_signup);

        progressBar = findViewById(R.id.progressbar_signup);

        loginHereSignUp = findViewById(R.id.loginhere_signup);
        loginHereSignUp.setOnClickListener(this);

        signUpBtn = findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginhere_signup:
                startActivity(new Intent(SignUp.this, MainActivity.class));
                break;

            case R.id.signup_btn:
                regesterUser();
                break;
        }
    }

    private void regesterUser() {

        String emailInput = email.getText().toString().trim();
        String nameInput = fullName.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String writeAPIkeyInput = writeAPIkey.getText().toString().trim();
        String readAPIkeyInput = readAPIkey.getText().toString().trim();
        String channelIdInput = channelId.getText().toString().trim();


        if (nameInput.isEmpty()) {
            fullName.setError("Please enter your Full name");
            fullName.requestFocus();
            return;
        }

        if (emailInput.isEmpty()) {
            email.setError("Email is Required");
            email.requestFocus();
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please Enter a Valid Email");
            email.requestFocus();
            return;
        }

        if (passwordInput.isEmpty()) {
            password.setError("Password is Required");
            password.requestFocus();
            return;
        } else if (passwordInput.length() < 6) {
            password.setError("Password must be greater than 5 Characters");
            password.requestFocus();
            return;
        }

        if (writeAPIkeyInput.isEmpty()) {
            writeAPIkey.setError("This Field is Required");
            writeAPIkey.requestFocus();
            return;
        } else if (writeAPIkeyInput.length() < 10) {
            writeAPIkey.setError("Enter a Valid API key");
            writeAPIkey.requestFocus();
            return;
        }

        if (readAPIkeyInput.isEmpty()) {
            readAPIkey.setError("This Field is Required");
            readAPIkey.requestFocus();
            return;
        } else if (readAPIkeyInput.length() < 10) {
            readAPIkey.setError("Enter a Valid API key");
            readAPIkey.requestFocus();
            return;
        }

        if (channelIdInput.isEmpty()) {
            channelId.setError("This Field is Required!");
            channelId.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            UserData userData = new UserData(nameInput, emailInput, passwordInput,
                                    writeAPIkeyInput, readAPIkeyInput, channelIdInput,
                                    minTemp, maxTemp, false);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification();

                                        Toast.makeText(SignUp.this, "User has been Registered Successfully, Check your email to verify it's you", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(SignUp.this, MainActivity.class));

                                    } else {
                                        Toast.makeText(SignUp.this, "Registration Failed!, Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                }
                            });


                        } else {

                            Toast.makeText(SignUp.this, "Registration Failed!, Something Went Wrong", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });


    }
}