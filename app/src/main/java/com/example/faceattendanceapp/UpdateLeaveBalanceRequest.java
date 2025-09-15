package com.example.faceattendanceapp;

public class UpdateLeaveBalanceRequest {
    private String emp_id;
    private int total_leaves;

    public UpdateLeaveBalanceRequest(String emp_id, int total_leaves) {
        this.emp_id = emp_id;
        this.total_leaves = total_leaves;
    }

    // Getters and setters if needed
}

