package com.example.betasolutions.model;

public class TaskEmployee {
    private Long tseId; //Primary Key
    private Long taskId; //foreign key til Task
    private String employeeId; //foreign key til Employee
    private double hoursWorked;

    public Long getTseId() {
        return tseId;
    }

    public void setTseId(Long tseId) {
        this.tseId = tseId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}
