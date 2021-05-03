package com.jagadeesh.smartfarming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    TextView email;
    Button passwordResetBtn;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email_password_reset);
        progressBar = findViewById(R.id.progressbar_password_reset);

        mAuth = FirebaseAuth.getInstance();

        passwordResetBtn = findViewById(R.id.password_reset_btn);
        passwordResetBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.password_reset_btn:

                ResetPassword();
                break;

        }

    }

    private void ResetPassword() {

        String emailInput = email.getText().toString().trim();

        if (emailInput.isEmpty()) {

            email.setError("Please enter your Email");
            email.requestFocus();
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {

            email.setError("Please enter a valid Email");
            email.requestFocus();
            return;

        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(emailInput).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();

                } else {

                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotPassword.this, "Try again! Something Went Wrong!", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}