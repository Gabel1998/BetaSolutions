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

    /**
     * Returns the workload per employee per day in the form of:
     * Map<EmployeeID, Map<Date, Pair<hours, workloadPercentage>>>
     */
    public Map<Long, Map<LocalDate, Pair<Double, Double>>> getEmployeeLoadOverTime() {
        List<TaskEmployee> assignments = taskEmployeeRepository.findAll();
        System.out.println("🔍 Found " + assignments.size() + " task-employee rows:");
        for (TaskEmployee a : assignments) {
            System.out.println("   ▶ " + a.getTseId()
                    + " emp=" + a.getEmployeeId()
                    + " hrs=" + a.getHoursWorked()
                    + " start=" + a.getStartDate()
                    + " end=" + a.getEndDate());
        }

//        List<TaskEmployee> assignments = taskEmployeeRepository.findAll();
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
            Employees emp = employeeRepository.getEmployeeById(employeeId);
            if (emp == null) {
                System.out.println("⚠️ Skipping employee ID " + employeeId + " because it doesn't exist in db_employees.");
                continue;
            }

            double maxPerDay = 8.0; // Standard workday hours
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

    /** Retrieves logged hours for a specific employee, formatted for the dashboard display */
    public List<Map<String, Object>> getLoggedHoursForEmployee(Long employeeId) {

        List<TaskEmployee> assignments = taskEmployeeRepository.findByEmployeeId(employeeId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (TaskEmployee assignment : assignments) {
            Long taskId = assignment.getTaskId();
            if (taskId == null) continue; // Skip if taskId is null

            // Create result entry
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", assignment.getTseId());
            entry.put("hours", assignment.getHoursWorked());

            // Get task information
            taskService.getTaskById(taskId).ifPresent(task -> {
                entry.put("taskName", task.getName());

                // Get subproject information
                Integer subprojectId = task.getSubProjectId();
                if (subprojectId != null) {
                    subProjectService.getSubProjectById(subprojectId).ifPresent(subproject -> {
                        entry.put("subProjectName", subproject.getName());

                        // Get project information
                        Integer projectId = subproject.getProjectId();
                        if (projectId != null) {
                            projectService.getProjectById(projectId).ifPresent(project ->
                                entry.put("projectName", project.getName())
                            );
                        }
                    });
                }
            });

            // Add default values if any information is missing
            if (!entry.containsKey("taskName")) entry.put("taskName", "Unknown Task");
            if (!entry.containsKey("subProjectName")) entry.put("subProjectName", "Unknown Subproject");
            if (!entry.containsKey("projectName")) entry.put("projectName", "Unknown Project");

            result.add(entry);
        }

        return result;
    }

    /** Log hours for a specific task and employee */
    public void logHours(long taskId, long employeeId, double hoursWorked) {
        taskEmployeeRepository.logHours(taskId, employeeId, hoursWorked);
    }

}