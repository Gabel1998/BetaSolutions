package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This controller handles the workflow for employees to log hours against tasks,
 * view logged hours, and manage timesheet entries.
 */
@Controller
@RequestMapping("/logs")
public class LogController {

    private final TaskEmployeeService taskEmployeeService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final TaskEmployeeRepository taskEmployeeRepository;
    private final SubProjectService subProjectService;

    // Constructor - dependency injection
    public LogController(TaskEmployeeService taskEmployeeService,
                         EmployeeService employeeService,
                         ProjectService projectService,
                         TaskService taskService,
                         TaskRepository taskRepository,
                         TaskEmployeeRepository taskEmployeeRepository,
                         SubProjectService subProjectService) {
        this.taskEmployeeService = taskEmployeeService;
        this.employeeService = employeeService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.taskEmployeeRepository = taskEmployeeRepository;
        this.subProjectService = subProjectService;
    }

    // Check if user is logged in
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // Entry point: select employee and project
    @GetMapping
    public String showLogSelection(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("projects", projectService.getAllProjects());
        return "logs/select";
    }

    // Step 2: select subproject for the chosen project
    @GetMapping("/select")
    public String showSubprojectsAndTasks(@RequestParam long employeeId,
                                          @RequestParam int projectId,
                                          Model model,
                                          HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        session.setAttribute("employeeId", employeeId);
        session.setAttribute("projectId", projectId);

        List<SubProject> subprojects = subProjectService.getAllSubProjectsByProjectId(projectId);
        if (subprojects.isEmpty()) {
            return "redirect:/logs?emptySubprojects";
        }

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("subprojects", subprojects);
        return "logs/subproject-selection";
    }

    // Step 3: show tasks and log hours form
    @GetMapping("/fill")
    public String showTaskLogForm(@RequestParam int subProjectId,
                                  Model model,
                                  HttpSession session) {
        Long employeeId = (Long) session.getAttribute("employeeId");
        if (employeeId == null) {
            return "redirect:/logs";
        }

        List<Task> tasks = taskRepository.findBySubProjectId(subProjectId);
        for (Task task : tasks) {
            Double logged = taskEmployeeRepository.getLoggedHoursForTaskAndEmployee(task.getId(), employeeId);
            task.setPrefilledHours(logged == null ? 0.0 : logged);
        }
        if (tasks.isEmpty()) {
            return "redirect:/logs/select?employeeId=" + employeeId + "&projectId=" + session.getAttribute("projectId") + "&emptyTasks=true";
        }

        model.addAttribute("tasks", tasks);
        return "logs/list";
    }

    // Dashboard: view logged hours with optional employee filter
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(required = false) Long employeeId,
                                Model model,
                                HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        // Add employees to dropdown
        model.addAttribute("employees", employeeService.getAllEmployees());

        if (employeeId != null) {
            Optional<Employees> employeeOptional = employeeService.getEmployeeById(employeeId);
            employeeOptional.ifPresent(employee -> {
                // Create a map with the employee data in the format the template expects
                Map<String, Object> selectedEmployee = new HashMap<>();
                selectedEmployee.put("id", employee.getEmId());
                selectedEmployee.put("name", employee.getEmFirstName() + " " + employee.getEmLastName());
                model.addAttribute("selectedEmployee", selectedEmployee);
            });
        }

        // Task overview for all tasks
        List<Task> allTasks = taskService.getAllTasks();

        List<Map<String, Object>> taskOverview = new ArrayList<>();
        for (Task task : allTasks) {
            double totalHours = taskService.getTotalHoursForTask(task.getId());

            Map<String, Object> row = new HashMap<>();
            row.put("projectName",
                    (task.getProjectId() != null)
                            ? projectService.getProjectById(task.getProjectId())
                            .map(Project::getName)
                            .orElse("Unknown Project")
                            : "Unknown Project"
            );

            row.put("subProjectName",
                    (task.getSubProjectId() != null)
                            ? subProjectService.getSubProjectById(task.getSubProjectId())
                            .map(SubProject::getName)
                            .orElse("Unknown Subproject")
                            : "Unknown Subproject");

            row.put("taskName", task.getName());
            row.put("loggedHours", totalHours);

            taskOverview.add(row);
        }

        model.addAttribute("overview", taskOverview);
        return "logs/dashboard";
    }

    // Save logged hours from form submission
    @PostMapping
    public String submitLog(@RequestParam Map<String, String> params, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        Long employeeId = (Long) session.getAttribute("employeeId");
        if (employeeId == null) {
            return "redirect:/logs";
        }

        params.forEach((key, value) -> {
            if (key.startsWith("hours_") && !value.isBlank()) {
                long taskId = Long.parseLong(key.replace("hours_", ""));
                double hoursWorked = Double.parseDouble(value);
                taskEmployeeService.logHours(taskId, employeeId, hoursWorked);
            }
        });
        return "redirect:/logs?success";
    }

    // Delete a time log entry
    @PostMapping("/delete")
    public String deleteLog(@RequestParam("logId") Long logId,
                            @RequestParam(required = false) Long employeeId,
                            HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        if (logId != null) {
            taskEmployeeRepository.delete(logId);
        }

        // Redirect back to the dashboard with same employee selected
        if (employeeId != null) {
            return "redirect:/logs/dashboard?employeeId=" + employeeId;
        } else {
            return "redirect:/logs/dashboard";
        }
    }
}
