package com.example.betasolutions.controller;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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

        List<Task> tasks = taskService.getTasksWithLoggedHoursBySubProject(subProjectId, employeeId);
            if (tasks.isEmpty()) {
                return "redirect:/logs/select?employeeId=" + employeeId + "&projectId=" + session.getAttribute("projectId") + "&emptyTasks=true";
            }

        model.addAttribute("tasks", tasks);
        return "logs/list";
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
