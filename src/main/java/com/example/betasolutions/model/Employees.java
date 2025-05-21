package com.example.betasolutions.model;

import java.time.LocalDateTime;

public class Employees {
    private Integer emId;
    private String emFirstName;
    private String emLastName;
    private String emUsername;
    private String emPassword;
    private double emEfficiency;
    private double maxWeeklyHours = 40.0;
    private LocalDateTime emCreatedAt;
    private LocalDateTime emUpdatedAt;

    // ─────────── Getters and Setters ───────────

    public Integer getEmId() {
        return emId;
    }

    public void setEmId(Integer emId) {
        this.emId = emId;
    }

    public String getEmFirstName() {
        return emFirstName;
    }

    public void setEmFirstName(String emFirstName) {
        this.emFirstName = emFirstName;
    }

    public String getEmLastName() {
        return emLastName;
    }

    public void setEmLastName(String emLastName) {
        this.emLastName = emLastName;
    }

    public String getEmUsername() {
        return emUsername;
    }

    public void setEmUsername(String emUsername) {
        this.emUsername = emUsername;
    }

    public String getEmPassword() {
        return emPassword;
    }

    public void setEmPassword(String emPassword) {
        this.emPassword = emPassword;
    }

    public double getEmEfficiency() {
        return emEfficiency;
    }

    public void setEmEfficiency(double emEfficiency) {
        this.emEfficiency = emEfficiency;
    }

    public double getMaxWeeklyHours() {
        return maxWeeklyHours;
    }

    public void setMaxWeeklyHours(double maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
    }

    public LocalDateTime getEmCreatedAt() {
        return emCreatedAt;
    }

    public void setEmCreatedAt(LocalDateTime emCreatedAt) {
        this.emCreatedAt = emCreatedAt;
    }

    public LocalDateTime getEmUpdatedAt() {
        return emUpdatedAt;
    }

    public void setEmUpdatedAt(LocalDateTime emUpdatedAt) {
        this.emUpdatedAt = emUpdatedAt;
    }
}
