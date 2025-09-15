package com.example.faceattendanceapp;

public class RegisterRequest {
    private String emp_id;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String password;
    private String userId;

    public RegisterRequest(String emp_id, String name, String email, String phone,
                           String department, String designation, String password, String userId) {
        this.emp_id = emp_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.password = password;
        this.userId = userId;
    }
}

