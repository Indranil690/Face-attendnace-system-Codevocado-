package com.example.faceattendanceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LeaveRequestAdapter adapter;
    private FloatingActionButton fabLeaveBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_requests);

        recyclerView = findViewById(R.id.recyclerViewLeaveRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabLeaveBalance = findViewById(R.id.fabLeaveBalance);
        fabLeaveBalance.setOnClickListener(view -> showUpdateLeaveDialog(this));

        fetchAllLeaveRequests();
    }

    private void fetchAllLeaveRequests() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<LeaveStatusResponse> call = apiService.getAllLeaveRequests();

        call.enqueue(new Callback<LeaveStatusResponse>() {
            @Override
            public void onResponse(Call<LeaveStatusResponse> call, Response<LeaveStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeaveRequest> requests = response.body().getRequests();

                    if (requests == null || requests.isEmpty()) {
                        Toast.makeText(LeaveRequestsActivity.this, "No leave requests found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    adapter = new LeaveRequestAdapter(LeaveRequestsActivity.this, requests, true);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(LeaveRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    Log.e("LEAVE_REQUESTS_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LeaveStatusResponse> call, Throwable t) {
                Toast.makeText(LeaveRequestsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LEAVE_REQUESTS_FAILURE", t.getMessage(), t);
            }
        });
    }

    private void showUpdateLeaveDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Leave Balance");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_leave_balance, null);
        EditText editTextEmpId = dialogView.findViewById(R.id.editTextEmpId);
        EditText editTextTotalLeaves = dialogView.findViewById(R.id.editTextTotalLeaves);
        builder.setView(dialogView);

        builder.setPositiveButton("Update", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String empId = editTextEmpId.getText().toString().trim();
            String totalLeavesStr = editTextTotalLeaves.getText().toString().trim();

            if (empId.isEmpty() || totalLeavesStr.isEmpty()) {
                Toast.makeText(context, "Please fill both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalLeaves;
            try {
                totalLeaves = Integer.parseInt(totalLeavesStr);
                if (totalLeaves < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Enter valid non-negative number for total leaves", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
            UpdateLeaveBalanceRequest request = new UpdateLeaveBalanceRequest(empId, totalLeaves);

            Call<ApiResponse> call = apiService.updateLeaveBalance(request);

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        // 🆕 Fetch and show updated leave balance
                        Call<LeaveBalanceResponse> balanceCall = apiService.getLeaveBalanceById(empId);
                        balanceCall.enqueue(new Callback<LeaveBalanceResponse>() {
                            @Override
                            public void onResponse(Call<LeaveBalanceResponse> call, Response<LeaveBalanceResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    LeaveBalanceResponse balance = response.body();

                                    String msg = "Total Leaves: " + balance.getTotalLeaves() + "\n"
                                            + "Used Leaves: " + balance.getUsedLeaves() + "\n"
                                            + "Remaining Leaves: " + balance.getRemainingLeaves();

                                    new AlertDialog.Builder(context)
                                            .setTitle("Updated Leave Balance for ID: " + empId)
                                            .setMessage(msg)
                                            .setPositiveButton("OK", null)
                                            .show();
                                } else {
                                    Toast.makeText(context, "Updated but failed to fetch balance", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LeaveBalanceResponse> call, Throwable t) {
                                Toast.makeText(context, "Balance fetch error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Failed to update leave balance", Toast.LENGTH_SHORT).show();
                        Log.e("LEAVE_BALANCE_UPDATE", "Code: " + response.code() + ", Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "API Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("LEAVE_BALANCE_ERROR", t.getMessage(), t);
                }
            });
        });
    }
}
