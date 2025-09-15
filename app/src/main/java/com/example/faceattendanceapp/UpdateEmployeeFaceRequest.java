package com.example.faceattendanceapp;

public class UpdateEmployeeFaceRequest {
    private String emp_id;
    private String photo;

    public UpdateEmployeeFaceRequest(String emp_id, String photo) {
        this.emp_id = emp_id;
        this.photo = photo;
    }
}

