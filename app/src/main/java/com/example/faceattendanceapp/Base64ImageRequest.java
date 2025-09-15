package com.example.faceattendanceapp;

public class Base64ImageRequest {
    private String userId;
    private String photo;      // must be "data:image/jpeg;base64,<base64>"
    private String latitude;
    private String longitude;

    public Base64ImageRequest() {}

    public Base64ImageRequest(String userId, String photo, String latitude, String longitude) {
        this.userId = userId;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters & setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
}
