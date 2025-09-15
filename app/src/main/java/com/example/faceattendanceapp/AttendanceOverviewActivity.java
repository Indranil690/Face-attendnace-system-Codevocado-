package com.example.faceattendanceapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceOverviewActivity extends AppCompatActivity {

    private TextView tvPresentCount, tvAbsentCount, tvLeaveCount, tvAttendanceRate, tvLateArrivals;
    private ProgressBar progressAttendanceRate;
    private RecyclerView recyclerWeeklyData, recyclerMonthlyData, recyclerDepartments;
    private LinearLayout layoutOnLeave;

    private WeeklyAdapter weeklyAdapter;
    private MonthlyAdapter monthlyAdapter;
    private DepartmentAdapter departmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_overview);

        // Bind Views
        tvPresentCount = findViewById(R.id.tvPresentCount);
        tvAbsentCount = findViewById(R.id.tvAbsentCount);
        tvLeaveCount = findViewById(R.id.tvLeaveCount);
        tvAttendanceRate = findViewById(R.id.tvAttendanceRate);
        tvLateArrivals = findViewById(R.id.tvLateArrivals);
        progressAttendanceRate = findViewById(R.id.progressAttendanceRate);
        layoutOnLeave = findViewById(R.id.layoutOnLeave);

        recyclerWeeklyData = findViewById(R.id.recyclerWeeklyData);
        recyclerMonthlyData = findViewById(R.id.recyclerMonthlyData);
        recyclerDepartments = findViewById(R.id.recyclerDepartments);

        // Set Layout Managers
        recyclerWeeklyData.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerMonthlyData.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerDepartments.setLayoutManager(new LinearLayoutManager(this));

        // Load Dashboard
        loadAttendanceDashboard();

        // On Leave Count Click
        layoutOnLeave.setOnClickListener(v -> fetchTodayOnLeaveEmployees());
    }

    private void loadAttendanceDashboard() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<AttendanceDashboardResponse> call = apiService.getAttendanceDashboard();

        call.enqueue(new Callback<AttendanceDashboardResponse>() {
            @Override
            public void onResponse(Call<AttendanceDashboardResponse> call, Response<AttendanceDashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceDashboardResponse data = response.body();

                    tvPresentCount.setText(String.valueOf(data.getPresentToday()));
                    tvAbsentCount.setText(String.valueOf(data.getAbsentToday()));
                    tvLeaveCount.setText(String.valueOf(data.getOnLeave()));
                    tvLateArrivals.setText("Late Arrivals: " + data.getLateArrivals());

                    float rate = data.getAttendanceRate();
                    tvAttendanceRate.setText("Attendance Rate: " + (int) rate + "%");
                    progressAttendanceRate.setProgress((int) rate);

                    weeklyAdapter = new WeeklyAdapter(data.getWeeklyData());
                    recyclerWeeklyData.setAdapter(weeklyAdapter);

                    monthlyAdapter = new MonthlyAdapter(data.getMonthly());
                    recyclerMonthlyData.setAdapter(monthlyAdapter);

                    departmentAdapter = new DepartmentAdapter(data.getDepartments());
                    recyclerDepartments.setAdapter(departmentAdapter);

                } else {
                    Toast.makeText(AttendanceOverviewActivity.this, "Failed to load dashboard data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AttendanceDashboardResponse> call, Throwable t) {
                Toast.makeText(AttendanceOverviewActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    private void fetchTodayOnLeaveEmployees() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<TodayLeaveResponse> call = apiService.getTodayOnLeave();  // ✅ corrected method name

        call.enqueue(new Callback<TodayLeaveResponse>() {
            @Override
            public void onResponse(Call<TodayLeaveResponse> call, Response<TodayLeaveResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TodayLeaveResponse leaveResponse = response.body();

                    if (leaveResponse.getCount() == 0 || leaveResponse.getEmployees() == null || leaveResponse.getEmployees().isEmpty()) {
                        Toast.makeText(AttendanceOverviewActivity.this, "No one is on leave today.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    StringBuilder message = new StringBuilder();
                    for (Employee emp : leaveResponse.getEmployees()) {
                        message.append("• ").append(emp.getName()).append("\n");
                    }

                    new AlertDialog.Builder(AttendanceOverviewActivity.this)
                            .setTitle("Employees on Leave Today")
                            .setMessage(message.toString())
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(AttendanceOverviewActivity.this, "Failed to fetch leave data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodayLeaveResponse> call, Throwable t) {
                Toast.makeText(AttendanceOverviewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LEAVE_API", t.getMessage(), t);
            }
        });
    }
}
