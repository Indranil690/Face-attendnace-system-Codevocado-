package com.example.faceattendanceapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText;
    Button sendResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        sendResetButton = findViewById(R.id.sendResetButton);

        sendResetButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // 🔁 Replace with API call later
                Toast.makeText(this, "Reset instructions sent to " + email, Toast.LENGTH_LONG).show();

                // Optional: Finish activity and return to login screen
                finish();
            }
        });
    }
}
