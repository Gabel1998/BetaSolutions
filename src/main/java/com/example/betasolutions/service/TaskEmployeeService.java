package com.example.betasolutions.service;

import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.EmployeeRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TaskEmployeeService {

    private final TaskEmployeeRepository taskEmployeeRepository;
    private final EmployeeRepository employeeRepository;

    public TaskEmployeeService(TaskEmployeeRepository taskEmployeeRepository, EmployeeRepository employeeRepository) {
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

        // Trin 1: Beregn antal timer per dag for hver medarbejder
        for (TaskEmployee assignment : assignments) {
            Long employeeId = assignment.getTaskId(); // eller assignment.getEmployeeId() hvis det findes
            LocalDate start = assignment.getStartDate();
            LocalDate end = assignment.getEndDate();


            double hours = assignment.getHoursWorked();

            long days = ChronoUnit.DAYS.between(start, end) + 1;
            double hoursPerDay = hours / days;

            dailyHoursMap.putIfAbsent(employeeId, new TreeMap<>());
            Map<LocalDate, Double> employeeLoad = dailyHoursMap.get(employeeId);

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                employeeLoad.put(date, employeeLoad.getOrDefault(date, 0.0) + hoursPerDay);
            }
        }

        // Trin 2: Hent maxWeeklyHours og konverter til procent
        Map<Long, Map<LocalDate, Pair<Double, Double>>> result = new HashMap<>();

        for (Map.Entry<Long, Map<LocalDate, Double>> entry : dailyHoursMap.entrySet()) {
            long employeeId = entry.getKey();
            Employees emp = employeeRepository.getEmployeeById(employeeId);

            double maxPerDay = emp.getMaxWeeklyHours() / 5.0; // antag 5 arbejdsdage
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
}
