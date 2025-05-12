package com.example.betasolutions.service;

import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TaskEmployeeService {

    private final TaskEmployeeRepository taskEmployeeRepository;

    public TaskEmployeeService(TaskEmployeeRepository taskEmployeeRepository) {
        this.taskEmployeeRepository = taskEmployeeRepository;
    }

    /**
     * Returnerer belastning pr. medarbejder pr. dag i form af:
     * Map<MedarbejderID, Map<Dato, Timer den dag>>
     */
    public Map<String, Map<LocalDate, Double>> getEmployeeLoadOverTime() {
        List<TaskEmployee> assignments = taskEmployeeRepository.findAll();
        Map<String, Map<LocalDate, Double>> loadMap = new HashMap<>();

        for (TaskEmployee assignment : assignments) {
            String employeeId = assignment.getEmployeeId();
            LocalDate start = assignment.getStartDate();
            LocalDate end = assignment.getEndDate();
            double hours = assignment.getAllocatedHours();

            long days = ChronoUnit.DAYS.between(start, end) + 1;
            double hoursPerDay = hours / days;

            loadMap.putIfAbsent(employeeId, new TreeMap<>());
            Map<LocalDate, Double> dailyLoad = loadMap.get(employeeId);

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                dailyLoad.put(date, dailyLoad.getOrDefault(date, 0.0) + hoursPerDay);
            }
        }

        return loadMap;
    }
}
