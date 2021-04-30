package com.jagadeesh.smartfarming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signUpHere, forgotPassword;

    public static String passwordInput, emailInput;

    TextView email, password;
    Button loginBtn;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    CheckBox rememberMe;

    public static boolean rememberMeBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = sharedPreferences.getString("remember", "");

        if (checkbox.equals("true")) {
            startActivity(new Intent(MainActivity.this, HomePage.class));
        }

        signUpHere = findViewById(R.id.signuphere_login);
        signUpHere.setOnClickListener(this);

        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);

        forgotPassword = findViewById(R.id.forgot_password_login);
        forgotPassword.setOnClickListener(this);

        progressBar = findViewById(R.id.progressbar_login);
        firebaseAuth = FirebaseAuth.getInstance();


        rememberMe = findViewById(R.id.checkbox_login);
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {

                    rememberMeBool = true;

                } else if (!buttonView.isChecked()) {

                    rememberMeBool = false;

                }
            }
        });


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signuphere_login:
                startActivity(new Intent(MainActivity.this, SignUp.class));
                break;

            case R.id.login_btn:
                UserLogin();
                break;

            case R.id.forgot_password_login:
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));

        }

    }

    private void UserLogin() {
        emailInput = email.getText().toString().trim();
        passwordInput = password.getText().toString().trim();


        if (emailInput.isEmpty()) {
            email.setError("Email is Required");
            email.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Enter a Valid Email");
            email.requestFocus();
            return;
        }

        if (passwordInput.isEmpty()) {
            password.setError("Password is Required");
            password.requestFocus();
            return;

        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);

                    if (rememberMeBool) {

                        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("remember", "true");
                        editor.apply();

                    } else if (!rememberMeBool) {

                        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();

                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, HomePage.class));

                    } else {
                        Toast.makeText(MainActivity.this, "You still haven't verified your email", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to Login! Please Check Your Credentials or Internet Connection!.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

}