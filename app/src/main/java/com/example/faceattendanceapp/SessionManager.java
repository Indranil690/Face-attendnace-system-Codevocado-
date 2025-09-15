package com.example.faceattendanceapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_EMP_ID = "employee_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";  // ✅ Added

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ✅ Save session details
    public void saveSession(String token, String role) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);  // ✅ Mark user as logged in
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void saveEmployeeId(String empId) {
        editor.putString(KEY_EMP_ID, empId);
        editor.apply();
    }

    public String getEmployeeId() {
        return prefs.getString(KEY_EMP_ID, "");
    }

    // ✅ Check login state
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ✅ Logout
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
