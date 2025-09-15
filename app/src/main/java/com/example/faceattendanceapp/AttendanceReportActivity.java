package com.example.faceattendanceapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceReportActivity extends AppCompatActivity {

    private Button btnPickDate;
    private TextView tvSelectedDate;
    private RecyclerView recyclerAttendanceReport;
    private AttendanceReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        btnPickDate = findViewById(R.id.btnPickDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        recyclerAttendanceReport = findViewById(R.id.recyclerAttendanceReport);

        recyclerAttendanceReport.setLayoutManager(new LinearLayoutManager(this));

        btnPickDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AttendanceReportActivity.this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    tvSelectedDate.setText("Selected: " + date);
                    fetchAttendanceReport(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void fetchAttendanceReport(String date) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getAttendanceReport(date).enqueue(new Callback<List<AttendanceRecordReport>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecordReport>> call, Response<List<AttendanceRecordReport>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AttendanceRecordReport> records = response.body();
                    adapter = new AttendanceReportAdapter(records);
                    recyclerAttendanceReport.setAdapter(adapter);
                    recyclerAttendanceReport.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AttendanceReportActivity.this, "No data found for this date", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecordReport>> call, Throwable t) {
                Log.e("ATTENDANCE_REPORT", "Error: " + t.getMessage());
                Toast.makeText(AttendanceReportActivity.this, "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
