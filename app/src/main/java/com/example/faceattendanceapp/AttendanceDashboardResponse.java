package com.example.faceattendanceapp;

import java.util.List;

public class AttendanceDashboardResponse {

    private int absentToday;
    private float attendanceRate;
    private List<DepartmentData> departments;
    private int lateArrivals;
    private List<MonthlyData> monthly;
    private int onLeave;
    private int presentToday;
    private int totalEmployees;
    private List<WeeklyData> weeklyData;

    // Getters and Setters
    public int getAbsentToday() {
        return absentToday;
    }

    public void setAbsentToday(int absentToday) {
        this.absentToday = absentToday;
    }

    public float getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(float attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public List<DepartmentData> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentData> departments) {
        this.departments = departments;
    }

    public int getLateArrivals() {
        return lateArrivals;
    }

    public void setLateArrivals(int lateArrivals) {
        this.lateArrivals = lateArrivals;
    }

    public List<MonthlyData> getMonthly() {
        return monthly;
    }

    public void setMonthly(List<MonthlyData> monthly) {
        this.monthly = monthly;
    }

    public int getOnLeave() {
        return onLeave;
    }

    public void setOnLeave(int onLeave) {
        this.onLeave = onLeave;
    }

    public int getPresentToday() {
        return presentToday;
    }

    public void setPresentToday(int presentToday) {
        this.presentToday = presentToday;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public List<WeeklyData> getWeeklyData() {
        return weeklyData;
    }

    public void setWeeklyData(List<WeeklyData> weeklyData) {
        this.weeklyData = weeklyData;
    }

    // Inner Classes
    public static class DepartmentData {
        private int count;
        private String department;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }

    public static class MonthlyData {
        private String month;
        private float rate;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }
    }

    public static class WeeklyData {
        private String date;
        private int count;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
