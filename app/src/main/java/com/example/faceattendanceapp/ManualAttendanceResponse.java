package com.example.faceattendanceapp;

import com.google.gson.annotations.SerializedName;

public class ManualAttendanceResponse {

    @SerializedName("error")
    private String error;

    @SerializedName("next_allowed_time")
    private String nextAllowedTime;

    @SerializedName("next_allowed_timestamp")
    private long nextAllowedTimestamp;

    @SerializedName("remaining_hours")
    private int remainingHours;

    @SerializedName("remaining_minutes")
    private int remainingMinutes;

    @SerializedName("type")
    private String type;

    // Getters
    public String getError() {
        return error;
    }

    public String getNextAllowedTime() {
        return nextAllowedTime;
    }

    public long getNextAllowedTimestamp() {
        return nextAllowedTimestamp;
    }

    public int getRemainingHours() {
        return remainingHours;
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    public String getType() {
        return type;
    }
}
