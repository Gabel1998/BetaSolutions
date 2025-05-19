package com.example.betasolutions.controller;

import com.example.betasolutions.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/logs")
public class LogController {

    private final TaskEmployeeService taskEmployeeService;
    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;
    private final SubProjectService subProjectService;

    public LogController(TaskEmployeeService taskEmployeeService,
                         TaskService taskService,
                         EmployeeService employeeService,
                         ProjectService projectService,
                         SubProjectService subProjectService) {
        this.taskEmployeeService = taskEmployeeService;
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
        this.subProjectService = subProjectService;
    }

    // Step 1: Show employee and project selection
    @GetMapping
    public String showLogSelection(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("projects", projectService.getAllProjects());
        return "logs/select";
    }

    // Step 2: Show subprojects after employee and project are selected
    @GetMapping("/select")
    public String showSubprojectsAndTasks(@RequestParam long employeeId,
                                          @RequestParam int projectId,
                                          Model model) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("subprojects", subProjectService.getAllSubProjectsByProjectId(projectId));
        return "logs/subproject-selection";
    }

    @GetMapping("/fill")
    public String showLogFormForSubproject(
            @RequestParam long employeeId,
            @RequestParam int projectId,
            @RequestParam int subProjectId,
            Model model) {

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("tasks", taskService.getTasksBySubProjectId(subProjectId));

        return "logs/list"; // This will show your log form with tasks
    }



    // Step 3: Submit the logged hours
    @PostMapping
    public String submitLog(@RequestParam Map<String, String> params) {
        params.forEach((key, value) -> {
            if (key.startsWith("hours_") && !value.isBlank()) {
                long taskId = Long.parseLong(key.replace("hours_", ""));
                double hoursWorked = Double.parseDouble(value);
                long employeeId = 1L; // Replace this with real employee selection if available
                taskEmployeeService.logHours(taskId, employeeId, hoursWorked);
            }
        });
        return "redirect:/logs?success";
    }
}
