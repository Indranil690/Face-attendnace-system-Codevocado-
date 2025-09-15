package com.example.faceattendanceapp;

import com.google.gson.annotations.SerializedName;

public class AttendanceRecord {

    @SerializedName("candidate_id")
    private int candidateId;

    @SerializedName("id")
    private int id;

    @SerializedName("in_date")
    private String inDate;

    @SerializedName("in_latitude")
    private String inLatitude;

    @SerializedName("in_longitude")
    private String inLongitude;

    @SerializedName("in_time")
    private String inTime;

    @SerializedName("out_time")
    private String outTime;

    @SerializedName("status")
    private String status; // NEW FIELD

    public int getCandidateId() {
        return candidateId;
    }

    public int getId() {
        return id;
    }

    public String getInDate() {
        return inDate;
    }

    public String getInLatitude() {
        return inLatitude;
    }

    public String getInLongitude() {
        return inLongitude;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public String getStatus() {
        return status;
    }

    // Combine latitude and longitude as location
    public String getCheckInLocation() {
        if (inLatitude == null || inLongitude == null) {
            return "N/A";
        }
        return inLatitude + ", " + inLongitude;
    }

    // Calculate hours worked (simple version – assumes HH:mm format)
    public String getHoursWorked() {
        try {
            String[] inParts = inTime.split(":");
            String[] outParts = outTime.split(":");

            int inHour = Integer.parseInt(inParts[0]);
            int inMin = Integer.parseInt(inParts[1]);

            int outHour = Integer.parseInt(outParts[0]);
            int outMin = Integer.parseInt(outParts[1]);

            int totalInMin = inHour * 60 + inMin;
            int totalOutMin = outHour * 60 + outMin;

            int workedMin = totalOutMin - totalInMin;
            int hours = workedMin / 60;
            int minutes = workedMin % 60;

            return hours + "h " + minutes + "m";
        } catch (Exception e) {
            return "N/A";
        }
    }
}
