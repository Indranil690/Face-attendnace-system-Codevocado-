package com.example.faceattendanceapp;

public class LeaveRequest {
    private int id;
    private String start_date;
    private String end_date;
    private String reason;
    private String status;
    private String applied_at;

    // Optional: Add emp_id if your API includes it
    private String emp_id;

    public LeaveRequest(int id, String start_date, String end_date, String reason, String status, String applied_at) {
        this.id = id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reason = reason;
        this.status = status;
        this.applied_at = applied_at;
    }

    // Optional constructor with emp_id
    public LeaveRequest(int id, String emp_id, String start_date, String end_date, String reason, String status, String applied_at) {
        this.id = id;
        this.emp_id = emp_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reason = reason;
        this.status = status;
        this.applied_at = applied_at;
    }

    // ✅ Getters
    public int getId() {
        return id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getApplied_at() {
        return applied_at;
    }

    // ✅ Setter for status
    public void setStatus(String status) {
        this.status = status;
    }
}
