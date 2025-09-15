package com.example.faceattendanceapp;

import com.google.gson.annotations.SerializedName;

public class UpdateFaceRequest {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("emp_id")
    private String empId;

    @SerializedName("photo")
    private String photo;

    public UpdateFaceRequest(String userId, String empId, String photo) {
        this.userId = userId;
        this.empId = empId;
        this.photo = photo;
    }

    // Optionally add getters if needed by Retrofit or Gson
    public String getUserId() {
        return userId;
    }

    public String getEmpId() {
        return empId;
    }

    public String getPhoto() {
        return photo;
    }
}
