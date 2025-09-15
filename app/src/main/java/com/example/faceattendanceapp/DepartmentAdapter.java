package com.example.faceattendanceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    private List<AttendanceDashboardResponse.DepartmentData> departmentList;

    public DepartmentAdapter(List<AttendanceDashboardResponse.DepartmentData> departmentList) {
        this.departmentList = departmentList;
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department_attendance, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        AttendanceDashboardResponse.DepartmentData data = departmentList.get(position);
        holder.tvDepartment.setText(data.getDepartment());
        holder.tvCount.setText("Present: " + data.getCount());
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    public static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDepartment, tvCount;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}
