package com.example.faceattendanceapp;

public class LoginRequest {
    private String employeeId;
    private String password;

    public LoginRequest(String employeeId, String password) {
        this.employeeId = employeeId;
        this.password = password;
    }

    // (Optional) You can add getters if you want, but Retrofit uses field names directly.
}
