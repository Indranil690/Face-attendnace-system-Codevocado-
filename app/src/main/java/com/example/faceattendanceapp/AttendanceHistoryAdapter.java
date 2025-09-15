package com.example.faceattendanceapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder> {

    private List<AttendanceRecord> attendanceList;

    public AttendanceHistoryAdapter(List<AttendanceRecord> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewInTimeOnly, textViewOutTimeOnly;
        TextView tvHoursWorked, tvStatus;
        ImageView imageViewLocation;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewInTimeOnly = itemView.findViewById(R.id.textViewInTimeOnly);
            textViewOutTimeOnly = itemView.findViewById(R.id.textViewOutTimeOnly);
            tvHoursWorked = itemView.findViewById(R.id.tvHoursWorked);
            imageViewLocation = itemView.findViewById(R.id.imageViewLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    @NonNull
    @Override
    public AttendanceHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceHistoryAdapter.ViewHolder holder, int position) {
        AttendanceRecord record = attendanceList.get(position);

        // Set date and times
        holder.textViewDate.setText(record.getInDate());
        holder.textViewInTimeOnly.setText(record.getInTime() != null ? record.getInTime() : "N/A");
        holder.textViewOutTimeOnly.setText(record.getOutTime() != null ? record.getOutTime() : "N/A");
        holder.tvHoursWorked.setText(record.getHoursWorked() != null ? record.getHoursWorked() : "N/A");
        // Set location icon click
        String location = record.getCheckInLocation();
        if (location != null && location.contains(",")) {
            String[] latLng = location.split(",");
            if (latLng.length == 2) {
                String latitude = latLng[0].trim();
                String longitude = latLng[1].trim();
                holder.imageViewLocation.setOnClickListener(v -> {
                    String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Check-in Location)";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(v.getContext(), "Google Maps not found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            holder.imageViewLocation.setOnClickListener(v ->
                    Toast.makeText(v.getContext(), "Location not available", Toast.LENGTH_SHORT).show()
            );
        }


        // Set status
        String inTime = record.getInTime();
        String outTime = record.getOutTime();

        if ((inTime == null || inTime.isEmpty()) && (outTime == null || outTime.isEmpty())) {
            holder.tvStatus.setText("Absent");
        } else if ((inTime != null && !inTime.isEmpty()) && (outTime != null && !outTime.isEmpty())) {
            holder.tvStatus.setText("Present");

        } else {
            holder.tvStatus.setText("In Progress");


        }
    }


    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
}
