package com.example.faceattendanceapp;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    // ✅ Message returned by API (success or completed)
    @SerializedName(value = "message", alternate = {"massage"})
    private String message;

    // ✅ Type field (check-in, check-out, wait, completed)
    @SerializedName("type")
    private String type;

    // ✅ Next allowed time (for check-in/check-out rules)
    @SerializedName("next_allowed_time")
    private String nextAllowedTime;

    @SerializedName("next_allowed_timestamp")
    private Long nextAllowedTimestamp;

    // ✅ For next day check-out
    @SerializedName("next_day")
    private Boolean nextDay;

    // ✅ Error field (face not recognized, too early, etc.)
    @SerializedName("error")
    private String error;

    // ✅ Remaining wait time (for "too early to check-out")
    @SerializedName("remaining_seconds")
    private Integer remainingSeconds;


    // ---------------- Getters ---------------- //
    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getNextAllowedTime() {
        return nextAllowedTime;
    }

    public Long getNextAllowedTimestamp() {
        return nextAllowedTimestamp;
    }

    public Boolean getNextDay() {
        return nextDay;
    }

    public String getError() {
        return error;
    }

    public Integer getRemainingSeconds() {
        return remainingSeconds;
    }

    // ✅ Helper methods
    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

    public boolean isCheckIn() {
        return "check-in".equalsIgnoreCase(type);
    }

    public boolean isCheckOut() {
        return "check-out".equalsIgnoreCase(type);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(type);
    }

    public boolean isWait() {
        return "wait".equalsIgnoreCase(type);
    }
}
