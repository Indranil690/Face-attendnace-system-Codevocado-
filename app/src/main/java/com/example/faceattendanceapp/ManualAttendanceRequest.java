package com.example.faceattendanceapp;

public class ManualAttendanceRequest {
    private String userId;
    private String latitude;
    private String longitude;

    public ManualAttendanceRequest(String userId, String latitude, String longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
