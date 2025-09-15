package com.example.faceattendanceapp;

public class EmployeeChangePasswordRequest {
    private String empId;
    private String currentPassword;
    private String newPassword;

    public EmployeeChangePasswordRequest(String empId, String currentPassword, String newPassword) {
        this.empId = empId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}

