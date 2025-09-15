package com.example.faceattendanceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.WeeklyViewHolder> {

    private List<AttendanceDashboardResponse.WeeklyData> weeklyList;

    public WeeklyAdapter(List<AttendanceDashboardResponse.WeeklyData> weeklyList) {
        this.weeklyList = weeklyList;
    }

    @NonNull
    @Override
    public WeeklyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weekly_attendance, parent, false);
        return new WeeklyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyViewHolder holder, int position) {
        AttendanceDashboardResponse.WeeklyData data = weeklyList.get(position);
        holder.tvDate.setText(data.getDate());
        holder.tvCount.setText("Present: " + data.getCount());
    }

    @Override
    public int getItemCount() {
        return weeklyList.size();
    }

    public static class WeeklyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCount;

        public WeeklyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}
