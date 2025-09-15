package com.example.faceattendanceapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestLeaveActivity extends AppCompatActivity {

    private EditText editTextFromDate, editTextToDate, editTextReason;
    private Button buttonSubmitLeave;
    private RecyclerView leaveHistoryRecyclerView;

    private LeaveRequestAdapter adapter;
    private String empId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);

        editTextFromDate = findViewById(R.id.fromDateEditText);
        editTextToDate = findViewById(R.id.toDateEditText);
        editTextReason = findViewById(R.id.reasonEditText);
        buttonSubmitLeave = findViewById(R.id.submitButton);
        leaveHistoryRecyclerView = findViewById(R.id.leaveHistoryRecyclerView);
        leaveHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextFromDate.setOnClickListener(v -> showDatePicker(editTextFromDate));
        editTextToDate.setOnClickListener(v -> showDatePicker(editTextToDate));

        SessionManager sessionManager = new SessionManager(this);
        empId = sessionManager.getEmployeeId();

        if (empId == null || empId.isEmpty()) {
            Toast.makeText(this, "Employee ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonSubmitLeave.setOnClickListener(v -> submitLeaveRequest());
        fetchLeaveHistory();
    }

    private void showDatePicker(EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
                    targetEditText.setText(selectedDate);
                }, year, month, day);
        datePicker.show();
    }

    private void submitLeaveRequest() {
        String fromDate = editTextFromDate.getText().toString().trim();
        String toDate = editTextToDate.getText().toString().trim();
        String reason = editTextReason.getText().toString().trim();

        if (fromDate.isEmpty() || toDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        LeaveApplyRequest request = new LeaveApplyRequest(empId, fromDate, toDate, reason);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        buttonSubmitLeave.setEnabled(false);

        Call<ApiResponse> call = apiService.applyLeave(request); // 🔥 NO TOKEN

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                buttonSubmitLeave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RequestLeaveActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    editTextFromDate.setText("");
                    editTextToDate.setText("");
                    editTextReason.setText("");
                    fetchLeaveHistory();
                } else {
                    Toast.makeText(RequestLeaveActivity.this, "Failed to apply leave", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                buttonSubmitLeave.setEnabled(true);
                Toast.makeText(RequestLeaveActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchLeaveHistory() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        LeaveStatusRequest request = new LeaveStatusRequest(empId);

        apiService.getLeaveStatus(request).enqueue(new Callback<LeaveStatusResponse>() {
            @Override
            public void onResponse(Call<LeaveStatusResponse> call, Response<LeaveStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeaveRequest> requests = response.body().getRequests();
                    adapter = new LeaveRequestAdapter(RequestLeaveActivity.this, requests, false);
                    leaveHistoryRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(RequestLeaveActivity.this, "Could not load leave history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LeaveStatusResponse> call, Throwable t) {
                Toast.makeText(RequestLeaveActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LeaveHistory", t.getMessage(), t);
            }
        });
    }
}
