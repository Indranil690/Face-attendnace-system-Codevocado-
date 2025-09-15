package com.example.faceattendanceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.MonthlyViewHolder> {

    private List<AttendanceDashboardResponse.MonthlyData> monthlyList;

    public MonthlyAdapter(List<AttendanceDashboardResponse.MonthlyData> monthlyList) {
        this.monthlyList = monthlyList;
    }

    @NonNull
    @Override
    public MonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monthly_attendance, parent, false);
        return new MonthlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyViewHolder holder, int position) {
        AttendanceDashboardResponse.MonthlyData data = monthlyList.get(position);
        holder.tvMonth.setText(data.getMonth());
        holder.tvRate.setText("Rate: " + data.getRate() + "%");
    }

    @Override
    public int getItemCount() {
        return monthlyList.size();
    }

    public static class MonthlyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvRate;

        public MonthlyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvRate = itemView.findViewById(R.id.tvRate);
        }
    }
}
