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

    public TaskEmployeeService(
            TaskEmployeeRepository taskEmployeeRepository,
            EmployeeRepository employeeRepository) {
        this.taskEmployeeRepository = taskEmployeeRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Returnerer belastning pr. medarbejder pr. dag i form af:
     * Map<MedarbejderID, Map<Dato, Pair<timer, belastningsprocent>>>
     */
    public Map<Long, Map<LocalDate, Pair<Double, Double>>> getEmployeeLoadOverTime() {
        List<TaskEmployee> assignments = taskEmployeeRepository.findAll();
        Map<Long, Map<LocalDate, Double>> dailyHoursMap = new HashMap<>();

        for (TaskEmployee assignment : assignments) {
            Long employeeId = assignment.getEmployeeId();
            LocalDate start = assignment.getStartDate();
            LocalDate end = assignment.getEndDate();

            // Fallback: If no start or end, treat it as yesterday
            if (start == null && end == null) {
                start = end = LocalDate.now().minusDays(1);
            } else if (start == null) {
                start = end;
            } else if (end == null) {
                end = start;
            }

            double hours = assignment.getHoursWorked();
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            double hoursPerDay = hours / days;

            dailyHoursMap.putIfAbsent(employeeId, new TreeMap<>());
            Map<LocalDate, Double> loadMap = dailyHoursMap.get(employeeId);

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                double total = loadMap.getOrDefault(date, 0.0) + hoursPerDay;
                // Cap total per day at 24h – no human can work more.
                loadMap.put(date, Math.min(total, 24.0));
            }
        }

        // Convert to percentage using employee max
        Map<Long, Map<LocalDate, Pair<Double, Double>>> result = new HashMap<>();

        for (Map.Entry<Long, Map<LocalDate, Double>> entry : dailyHoursMap.entrySet()) {
            long employeeId = entry.getKey();
            Employees emp = employeeRepository.getEmployeeById(employeeId);
            if (emp == null) continue;

            double maxPerDay = emp.getMaxWeeklyHours() / 5.0;
            Map<LocalDate, Pair<Double, Double>> percentMap = new TreeMap<>();

            for (Map.Entry<LocalDate, Double> day : entry.getValue().entrySet()) {
                double actual = day.getValue();
                double percent = (actual / maxPerDay) * 100.0;
                percentMap.put(day.getKey(), Pair.of(actual, percent));
            }

            result.put(employeeId, percentMap);
        }

        return result;
    }

    public void logHours(long taskId, long employeeId, double hoursWorked) {
        taskEmployeeRepository.logHours(taskId, employeeId, hoursWorked);
    }

}