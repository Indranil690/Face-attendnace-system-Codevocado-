package com.example.faceattendanceapp;

public class ChangePasswordRequest {
    private String userId;
    private String currentPassword;
    private String newPassword;

    public ChangePasswordRequest(String userId, String currentPassword, String newPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
