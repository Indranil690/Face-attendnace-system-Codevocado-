package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceHistoryAdapter adapter;
    private CardView btnFaceAttendance, btnQRAttendance;
    private ApiService apiService;
    private String empId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize CardViews (attendance buttons)
        btnFaceAttendance = findViewById(R.id.btnFaceAttendance);
        btnQRAttendance = findViewById(R.id.btnQRAttendance);

        // API and session setup
        apiService = ApiClient.getClient(this).create(ApiService.class);
        SessionManager sessionManager = new SessionManager(this);
        empId = sessionManager.getEmployeeId();

        // Fetch attendance history
        fetchAttendanceHistory();

        // Click listener -> Face Attendance
        btnFaceAttendance.setOnClickListener(view -> {
            Intent intent = new Intent(AdminAttendanceActivity.this, AdminFaceAttendanceActivity.class);
            startActivity(intent);
        });

        // Click listener -> QR Attendance
        btnQRAttendance.setOnClickListener(view -> {
            Intent intent = new Intent(AdminAttendanceActivity.this, QRAttendanceActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAttendanceHistory() {
        if (empId == null || empId.isEmpty()) {
            Toast.makeText(this, "Employee ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject empIdJson = new JsonObject();
        empIdJson.addProperty("emp_id", empId);

        apiService.getAttendanceHistory(empIdJson).enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<AttendanceResponse> call, @NonNull Response<AttendanceResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getAttendanceList() != null) {
                    adapter = new AttendanceHistoryAdapter(response.body().getAttendanceList());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminAttendanceActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttendanceResponse> call, @NonNull Throwable t) {
                Toast.makeText(AdminAttendanceActivity.this, "Failed to load records", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
