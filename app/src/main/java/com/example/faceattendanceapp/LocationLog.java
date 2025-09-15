package com.example.faceattendanceapp;

public class LocationLog {
    private String employeeName;
    private String timestamp;
    private String location;

    public LocationLog(String employeeName, String timestamp, String location) {
        this.employeeName = employeeName;
        this.timestamp = timestamp;
        this.location = location;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }
}

