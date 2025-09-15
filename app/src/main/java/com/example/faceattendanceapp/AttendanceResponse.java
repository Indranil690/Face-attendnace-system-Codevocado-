package com.example.faceattendanceapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AttendanceResponse {

    @SerializedName("attendance")
    private List<AttendanceRecord> attendanceList;

    public List<AttendanceRecord> getAttendanceList() {
        return attendanceList;
    }
}
