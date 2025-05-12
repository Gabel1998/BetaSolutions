package com.example.betasolutions.model;

import java.time.LocalDateTime;

public class Employees {
    private Integer emId;
    private String emFirstName;
    private String emLastName;
    private double emEfficiency;
    private double maxWeeklyHours = 40.0; ///MANGLER i db og ER model. default value: 40timer/uge?
    private LocalDateTime emCreatedAt;
    private LocalDateTime emUpdatedAt;



    //getters og setters
    public void setEmId(Integer emId){
        this.emId = emId;
    }

    public Integer getEmId() {
        return emId;
    }

    public void setEmFirstName(String emFirstName) {
        this.emFirstName = emFirstName;
    }

    public String getEmFirstName() {
        return emFirstName;
    }

    public void setEmLastName(String emLastName) {
        this.emLastName = emLastName;
    }

    public String getEmLastName() {
        return emLastName;
    }

    public void setEmEfficiency(double emEfficiency) {
        this.emEfficiency = emEfficiency;
    }

    public double getEmEfficiency() {
        return emEfficiency;
    }

    public void setEmCreatedAt(LocalDateTime emCreatedAt) {
        this.emCreatedAt = emCreatedAt;
    }

    public LocalDateTime getEmCreatedAt() {
        return emCreatedAt;
    }

    public void setEmUpdatedAt(LocalDateTime emUpdatedAt) {
        this.emUpdatedAt = emUpdatedAt;
    }

    public LocalDateTime getEmUpdatedAt() {
        return emUpdatedAt;
    }


    public double getMaxWeeklyHours() {
        return maxWeeklyHours;
    }

    public void setMaxWeeklyHours(double maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
    }
}
