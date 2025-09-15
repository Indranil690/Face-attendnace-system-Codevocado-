package com.example.faceattendanceapp;

public class UpdateEmployeeRequest {
    private String name;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String userId;

    public UpdateEmployeeRequest(String name, String email, String phone, String department, String designation, String userId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.userId = userId;
    }
}
