package com.example.faceattendanceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private CardView btnUpdateFace, btnMarkFaceAttendance,  btnRequestLeave;

    private String empId;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LeaveNotificationPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_logout) {
                showLogoutDialog();
                return true;

            } else if (itemId == R.id.nav_toggle_theme) {
                ThemeManager.toggleTheme(this);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_change_password) {
                SessionManager sessionManager = new SessionManager(this);
                String empId = sessionManager.getEmployeeId();
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                intent.putExtra("IS_ADMIN", false);
                intent.putExtra("ID_VALUE", empId);
                startActivity(intent);
                return true;
            }

            return false;
        });

        TextView dashboardTitle = findViewById(R.id.dashboardTitle);
        dashboardTitle.setText(Html.fromHtml(getString(R.string.dashboard_title_styled), Html.FROM_HTML_MODE_LEGACY));

        // Bind Cards
        btnUpdateFace = findViewById(R.id.btnUpdateFace);
        btnMarkFaceAttendance = findViewById(R.id.btnMarkFaceAttendance);
        btnRequestLeave = findViewById(R.id.btnRequestLeave);



        // Card Listeners

        btnUpdateFace.setOnClickListener(v ->
                startActivity(new Intent(this, UpdateFaceImageActivity.class)));

        btnMarkFaceAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, MyAttendanceActivity.class)));

        btnRequestLeave.setOnClickListener(v ->
                startActivity(new Intent(this, RequestLeaveActivity.class)));





        // 🔔 Fetch and notify leave status changes
        SessionManager sessionManager = new SessionManager(this);
        empId = sessionManager.getEmployeeId();

        if (empId != null && !empId.isEmpty()) {
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            checkLeaveStatusForNotification();
        }
    }

    private void checkLeaveStatusForNotification() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        LeaveStatusRequest request = new LeaveStatusRequest(empId);

        apiService.getLeaveStatus(request).enqueue(new Callback<LeaveStatusResponse>() {
            @Override
            public void onResponse(Call<LeaveStatusResponse> call, Response<LeaveStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeaveRequest> requests = response.body().getRequests();

                    for (LeaveRequest leave : requests) {
                        String key = "leave_" + leave.getId();
                        String lastStatus = sharedPreferences.getString(key, "Pending");

                        if (!leave.getStatus().equalsIgnoreCase("Pending") &&
                                !leave.getStatus().equalsIgnoreCase(lastStatus)) {

                            String msg = leave.getStart_date() + " to " + leave.getEnd_date() +
                                    " leave " + leave.getStatus();
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

                            sharedPreferences.edit().putString(key, leave.getStatus()).apply();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaveStatusResponse> call, Throwable t) {
                Log.e("LEAVE_NOTIFY_FAIL", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // ✅ Clear session
                    SessionManager sessionManager = new SessionManager(MainActivity.this);
                    sessionManager.clearSession();

                    // ✅ Go back to LoginActivity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
