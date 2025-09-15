package com.example.faceattendanceapp;

public class LeaveDecisionRequest {
    private int leave_id;
    private String action;
    private String admin_id;

    public LeaveDecisionRequest(int leave_id, String action, String admin_id) {
        this.leave_id = leave_id;
        this.action = action;
        this.admin_id = admin_id;
    }
}
