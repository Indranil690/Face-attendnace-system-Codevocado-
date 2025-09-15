package com.example.faceattendanceapp;

import java.util.List;

public class TodayLeaveResponse {
    private int count;
    private List<Employee> employees;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
