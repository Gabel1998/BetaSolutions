package com.example.betasolutions.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private Long id; //Primary Key

    @NotNull(message = "Project ID is required")
    private Long projectId; //foreign key til projects
    private Integer subProjectId; //foreign key til subProjects

    @NotBlank(message = "Task name cannot be empty")
    @Size(max = 100, message = "Task name must be under 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    @NotNull(message = "Estimated hours is required")
    @PositiveOrZero(message = "Estimated hours must be 0 or more")
    private double estimatedHours;

    @PositiveOrZero(message = "Actual hours must be 0 or more")
    private double actualHours;

    private LocalDate startDate; // ny
    private LocalDate endDate;   // ny

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate StartDate) {
        this.startDate = StartDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate EndDate) {
        this.endDate = EndDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSubProjectId() {
        return subProjectId;
    }

    public void setSubProjectId(Integer subProjectId) {
        this.subProjectId = subProjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public double getActualHours() {
        return actualHours;
    }

    public void setActualHours(double actualHours) {
        this.actualHours = actualHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public int getPercentComplete() {
        if (estimatedHours == 0) {
            return 0; // Avoid division by zero
        }
        return (int) ((actualHours / estimatedHours) * 100);
    }

    public String getAssignedTo() {
        /// Mangler logik til at assigne employee(s) til task
        return "Assigned to employee"; // placeholder
    }
}
