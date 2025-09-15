package com.example.faceattendanceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceReportAdapter extends RecyclerView.Adapter<AttendanceReportAdapter.ReportViewHolder> {

    private List<AttendanceRecordReport> reportList;

    public AttendanceReportAdapter(List<AttendanceRecordReport> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        AttendanceRecordReport record = reportList.get(position);

        holder.tvName.setText(record.getName());
        holder.tvEmpId.setText("ID: " + record.getEmployeeId());
        holder.tvDept.setText("Dept: " + record.getDepartment());
        holder.tvInTime.setText("In: " + record.getInTime());
        holder.tvOutTime.setText("Out: " + record.getOutTime());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmpId, tvDept, tvInTime, tvOutTime;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmpId = itemView.findViewById(R.id.tvEmpId);
            tvDept = itemView.findViewById(R.id.tvDept);
            tvInTime = itemView.findViewById(R.id.tvInTime);
            tvOutTime = itemView.findViewById(R.id.tvOutTime);
        }
    }
}
