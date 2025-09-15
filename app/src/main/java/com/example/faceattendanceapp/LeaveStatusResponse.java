package com.example.faceattendanceapp;

import java.util.List;

public class LeaveStatusResponse {
    private List<LeaveRequest> requests;

    public List<LeaveRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<LeaveRequest> requests) {
        this.requests = requests;
    }
}
