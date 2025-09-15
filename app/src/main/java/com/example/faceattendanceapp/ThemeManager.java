package com.example.faceattendanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_DARK_MODE = "is_dark";

    // ✅ Apply the saved theme when an activity starts
    public static void applySavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK_MODE, false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    // ✅ Toggle dark/light mode and restart app to apply everywhere
    public static void toggleTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK_MODE, false);

        boolean newMode = !isDark;
        prefs.edit().putBoolean(KEY_DARK_MODE, newMode).apply();

        AppCompatDelegate.setDefaultNightMode(
                newMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        Toast.makeText(context, newMode ? "Dark mode ON" : "Light mode ON", Toast.LENGTH_SHORT).show();

        // ✅ Restart the entire app from SplashActivity to apply the theme globally
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
