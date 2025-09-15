package com.example.faceattendanceapp;

public class LeaveStatusRequest {
    private String emp_id;

    public LeaveStatusRequest(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }
}
