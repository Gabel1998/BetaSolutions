package com.example.betasolutions.dto;

import java.time.LocalDate;
import java.util.Map;

public class WorkloadPerEmployeeDTO {
    private final String employeeName;
    private final Map<LocalDate, Double> dailyLoadPercent;

    public WorkloadPerEmployeeDTO(String employeeName, Map<LocalDate, Double> dailyLoadPercent) {
        this.employeeName = employeeName;
        this.dailyLoadPercent = dailyLoadPercent;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Map<LocalDate, Double> getDailyLoadPercent() {
        return dailyLoadPercent;
    }
}
