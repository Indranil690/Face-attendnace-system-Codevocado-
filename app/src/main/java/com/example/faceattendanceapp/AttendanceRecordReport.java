package com.example.faceattendanceapp;

public class AttendanceRecordReport {
    private String department;
    private int emp_id;
    private String employee_id;
    private String name;
    private String in_date;
    private String in_time;
    private String out_time;
    private String in_latitude;
    private String in_longitude;

    // ✅ Standard camelCase Getters
    public String getDepartment() {
        return department;
    }

    public int getEmpId() {
        return emp_id;
    }

    public String getEmployeeId() {
        return employee_id;
    }

    public String getName() {
        return name;
    }

    public String getInDate() {
        return in_date;
    }

    public String getInTime() {
        return in_time;
    }

    public String getOutTime() {
        return out_time;
    }

    public String getInLatitude() {
        return in_latitude;
    }

    public String getInLongitude() {
        return in_longitude;
    }
}
