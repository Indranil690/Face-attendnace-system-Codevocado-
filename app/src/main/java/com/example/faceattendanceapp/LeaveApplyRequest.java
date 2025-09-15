package com.example.faceattendanceapp;

public class LeaveApplyRequest {
    private String emp_id;
    private String start_date;
    private String end_date;
    private String reason;

    public LeaveApplyRequest(String emp_id, String start_date, String end_date, String reason) {
        this.emp_id = emp_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reason = reason;
    }

    // Getters
    public String getEmpId() {
        return emp_id;
    }

    public String getStartDate() {
        return start_date;
    }

    public String getEndDate() {
        return end_date;
    }

    public String getReason() {
        return reason;
    }

    // Optional: Setters if needed later
    public void setEmpId(String emp_id) {
        this.emp_id = emp_id;
    }

    public void setStartDate(String start_date) {
        this.start_date = start_date;
    }

    public void setEndDate(String end_date) {
        this.end_date = end_date;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
