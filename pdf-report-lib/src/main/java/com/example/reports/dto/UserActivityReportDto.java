package com.example.reports.dto;

import java.util.List;

public class UserActivityReportDto {

    private String username;
    private List<String> activities;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
}
