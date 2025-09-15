package com.example.faceattendanceapp;

import com.google.gson.annotations.SerializedName;

public class Employee {

    @SerializedName("id")
    private int id;

    @SerializedName("emp_id")
    private String emp_id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("department")
    private String department;

    @SerializedName("designation")
    private String designation;

    public int getId() {
        return id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }
}
