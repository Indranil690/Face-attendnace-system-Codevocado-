package com.example.faceattendanceapp;

public class AdminLoginRequest {
    private String email;
    private String password;

    public AdminLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
