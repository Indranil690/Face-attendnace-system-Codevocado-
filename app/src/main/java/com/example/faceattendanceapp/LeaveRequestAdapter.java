package com.example.faceattendanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveRequestAdapter extends RecyclerView.Adapter<LeaveRequestAdapter.ViewHolder> {

    private Context context;
    private List<LeaveRequest> leaveRequestList;
    private boolean isAdmin; // 👈 New flag

    // ✅ Updated constructor to accept isAdmin flag
    public LeaveRequestAdapter(Context context, List<LeaveRequest> leaveRequestList, boolean isAdmin) {
        this.context = context;
        this.leaveRequestList = leaveRequestList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leave_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveRequest request = leaveRequestList.get(position);

        // Fallback: If emp_id is null, show the internal ID
        String displayEmpId = request.getEmp_id() != null ? request.getEmp_id() : String.valueOf(request.getId());

        holder.textViewEmployeeName.setText("Leave ID: " + request.getId());
        holder.textViewDateRange.setText(request.getStart_date() + " - " + request.getEnd_date());
        holder.textViewReason.setText("Reason: " + request.getReason());
        holder.textViewStatus.setText("Status: " + request.getStatus());

        // ✅ Show buttons only for admin and Pending status
        if (isAdmin && "Pending".equalsIgnoreCase(request.getStatus())) {
            holder.buttonApprove.setVisibility(View.VISIBLE);
            holder.buttonReject.setVisibility(View.VISIBLE);
        } else {
            holder.buttonApprove.setVisibility(View.GONE);
            holder.buttonReject.setVisibility(View.GONE);
        }

        holder.buttonApprove.setOnClickListener(v -> {
            sendLeaveDecision(request.getId(), "Approved", "1", position);
        });

        holder.buttonReject.setOnClickListener(v -> {
            sendLeaveDecision(request.getId(), "Rejected", "1", position);
        });
    }

    private void sendLeaveDecision(int leaveId, String action, String adminId, int position) {
        ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
        LeaveDecisionRequest decisionRequest = new LeaveDecisionRequest(leaveId, action, adminId);

        apiService.decideLeave(decisionRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    leaveRequestList.get(position).setStatus(action);
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaveRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEmployeeName, textViewDateRange, textViewReason, textViewStatus;
        Button buttonApprove, buttonReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEmployeeName = itemView.findViewById(R.id.textViewEmployeeName);
            textViewDateRange = itemView.findViewById(R.id.textViewDateRange);
            textViewReason = itemView.findViewById(R.id.textViewReason);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }
    }
}
