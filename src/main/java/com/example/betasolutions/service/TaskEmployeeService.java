package com.example.betasolutions.service;

import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TaskEmployeeService {

    private final TaskEmployeeRepository taskEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskService taskService;
    private final SubProjectService subProjectService;
    private final ProjectService projectService;

    // CONSTRUCTOR
    public TaskEmployeeService(
            TaskEmployeeRepository taskEmployeeRepository,
            EmployeeRepository employeeRepository,
            TaskService taskService,
            SubProjectService subProjectService,
            ProjectService projectService) {
        this.taskEmployeeRepository = taskEmployeeRepository;
        this.employeeRepository = employeeRepository;
        this.taskService = taskService;
        this.subProjectService = subProjectService;
        this.projectService = projectService;
    }

    // Retrieves the load of each employee over time, returning a map with employee IDs as keys
    public Map<Long, Map<LocalDate, Pair<Double, Double>>> getEmployeeLoadOverTime() {
        List<TaskEmployee> assignments = taskEmployeeRepository.findAll();
        Map<Long, Map<LocalDate, Double>> dailyHoursMap = new HashMap<>();

        // Step 1: Calculate hours per day for each employee
        for (TaskEmployee assignment : assignments) {
            Long employeeId = assignment.getEmployeeId();
            LocalDate start = assignment.getStartDate();
            LocalDate end = assignment.getEndDate();

            if (start == null || end == null) {
                continue;
            }

            double hours = assignment.getHoursWorked();

            // Calculate days between dates (including both start and end dates)
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            double hoursPerDay = hours / days;

            dailyHoursMap.putIfAbsent(employeeId, new TreeMap<>());
            Map<LocalDate, Double> employeeLoad = dailyHoursMap.get(employeeId);

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                employeeLoad.put(date, employeeLoad.getOrDefault(date, 0.0) + hoursPerDay);
            }
        }

        // Step 2: Get maxWeeklyHours and convert to percentage
        Map<Long, Map<LocalDate, Pair<Double, Double>>> result = new HashMap<>();

        for (Map.Entry<Long, Map<LocalDate, Double>> entry : dailyHoursMap.entrySet()) {
            long employeeId = entry.getKey();
            Optional<Employees> empOptional = employeeRepository.getEmployeeById(employeeId);

            if (empOptional.isEmpty()) {
                continue;
            }

            Employees emp = empOptional.get();

            // Assuming a standard 5-day work week to convert from weekly to daily
            double maxWeeklyHours = emp.getMaxWeeklyHours();
            double maxPerDay = maxWeeklyHours / 5.0; // Convert weekly hours to daily hours

            Map<LocalDate, Pair<Double, Double>> dailyWithPercent = new TreeMap<>();

            for (Map.Entry<LocalDate, Double> daily : entry.getValue().entrySet()) {
                double hours = daily.getValue();
                double percentage = (hours / maxPerDay) * 100.0;
                dailyWithPercent.put(daily.getKey(), Pair.of(hours, percentage));
            }

            result.put(employeeId, dailyWithPercent);
        }

        return result;
    }

    // logs hours worked by an employee on a specific task
    public void logHours(long taskId, long employeeId, double hoursWorked) {
        taskEmployeeRepository.logHours(taskId, employeeId, hoursWorked);
    }

}
