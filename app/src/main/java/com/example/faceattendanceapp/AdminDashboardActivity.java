package com.example.faceattendanceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.core.view.GravityCompat;


public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        // Menu icon opens drawer
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation item click
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_logout) {
                showLogoutDialog();
                return true;

            } else if (itemId == R.id.nav_toggle_theme) {
                ThemeManager.toggleTheme(this);
                Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_change_password) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
                return true;
            }

            return false;
        });

        // Dual-color title
        TextView tvTitle = findViewById(R.id.tvAdminDashboardTitle);
        tvTitle.setText(Html.fromHtml(getString(R.string.admin_dashboard_title_styled)));

        // Cards
        findViewById(R.id.cardManageEmployees).setOnClickListener(v ->
                startActivity(new Intent(this, ViewEmployeesActivity.class)));


        findViewById(R.id.cardMyAttendance).setOnClickListener(v ->
                startActivity(new Intent(this, AdminAttendanceActivity.class)));


        findViewById(R.id.cardAttendanceOverview).setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceOverviewActivity.class)));

        findViewById(R.id.cardLeaveRequests).setOnClickListener(v ->
                startActivity(new Intent(this, LeaveRequestsActivity.class)));

        findViewById(R.id.cardAdminProfile).setOnClickListener(v ->
                startActivity(new Intent(this, AdminProfileActivity.class)));

        // 🆕 Attendance Report
        findViewById(R.id.cardAttendanceReport).setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceReportActivity.class)));

        // 🔔 Check new leave requests after login
        checkNewLeaveRequests();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // ✅ Clear session
                    SessionManager sessionManager = new SessionManager(AdminDashboardActivity.this);
                    sessionManager.clearSession();

                    // ✅ Reset leave prefs if needed
                    getSharedPreferences("LeavePrefs", MODE_PRIVATE)
                            .edit().putInt("last_pending_count", 0).apply();

                    // ✅ Redirect to LoginActivity
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void checkNewLeaveRequests() {
        SessionManager sessionManager = new SessionManager(this);
        String adminId = sessionManager.getEmployeeId();

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        LeaveStatusRequest request = new LeaveStatusRequest(adminId);

        apiService.getLeaveStatus(request).enqueue(new Callback<LeaveStatusResponse>() {
            @Override
            public void onResponse(Call<LeaveStatusResponse> call, Response<LeaveStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeaveRequest> requests = response.body().getRequests();

                    int pendingCount = 0;
                    for (LeaveRequest leave : requests) {
                        if ("Pending".equalsIgnoreCase(leave.getStatus())) {
                            pendingCount++;
                        }
                    }

                    SharedPreferences prefs = getSharedPreferences("LeavePrefs", MODE_PRIVATE);
                    int lastPending = prefs.getInt("last_pending_count", 0);

                    if (pendingCount > lastPending) {
                        int newRequests = pendingCount - lastPending;
                        Toast.makeText(AdminDashboardActivity.this,
                                "🔔 " + newRequests + " new leave request(s)", Toast.LENGTH_LONG).show();
                    }

                    prefs.edit().putInt("last_pending_count", pendingCount).apply();

                } else {
                    Log.e("LeaveCheck", "Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LeaveStatusResponse> call, Throwable t) {
                Log.e("LeaveCheck", "Error: " + t.getMessage(), t);
            }
        });
    }
}
