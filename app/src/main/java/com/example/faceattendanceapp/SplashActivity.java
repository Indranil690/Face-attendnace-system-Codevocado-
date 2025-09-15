package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash);

        // ✅ Set dual-colored app name
        TextView title = findViewById(R.id.textAppName);
        title.setText(Html.fromHtml(getString(R.string.app_title_styled), Html.FROM_HTML_MODE_LEGACY));

        // ✅ Wait for 800ms, then check session and redirect
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);

            if (sessionManager.isLoggedIn()) {
                // Redirect based on role
                String role = sessionManager.getRole();
                if ("admin".equals(role)) {
                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                // Not logged in → go to Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish(); // Prevent back button from showing splash again
        }, 800);
    }
}
