package com.example.betasolutions.controller;

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

    public LogController(TaskEmployeeService taskEmployeeService,
                         EmployeeService employeeService,
                         ProjectService projectService, TaskService taskService, TaskRepository taskRepository, TaskEmployeeRepository taskEmployeeRepository,
                         SubProjectService subProjectService) {
        this.taskEmployeeService = taskEmployeeService;
        this.employeeService = employeeService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.taskEmployeeRepository = taskEmployeeRepository;
        this.subProjectService = subProjectService;
    }

    // Show employee and project selection
    @GetMapping
    public String showLogSelection(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("projects", projectService.getAllProjects());
        return "logs/select";
    }

    // Show subprojects after employee and project are selected
    @GetMapping("/select")
    public String showSubprojectsAndTasks(@RequestParam long employeeId,
                                          @RequestParam int projectId,
                                          Model model,
                                          HttpSession session) {
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

    // Show tasks after subproject is selected
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

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
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


    // Submit the logged hours
    @PostMapping
    public String submitLog(@RequestParam Map<String, String> params, HttpSession session) {
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
}