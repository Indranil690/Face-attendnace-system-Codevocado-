package com.example.faceattendanceapp;

public class LeaveBalanceResponse {
    private String emp_id;
    private int status;
    private int total_leaves;
    private int used_leaves;

    public String getEmpId() {
        return emp_id;
    }

    public int getStatus() {
        return status;
    }

    public int getTotalLeaves() {
        return total_leaves;
    }

    public int getUsedLeaves() {
        return used_leaves;
    }

    // Optional: Derived field if needed in UI
    public int getRemainingLeaves() {
        return total_leaves - used_leaves;
    }
}
