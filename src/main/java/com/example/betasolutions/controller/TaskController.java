package com.example.betasolutions.controller;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;

    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    @GetMapping
    public String listTasks(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("task", new Task());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        taskService.createTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "tasks/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        task.setId(id);
        taskService.updateTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    @GetMapping("/task/{id}/hours")
    public String getTaskHours(@PathVariable("id") Long taskId, Model model) {
       Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        double timer_d = taskService.getTotalHoursForTask(taskId);
        double dagrate = projectService.calculateDagRate(task.getProjectId());

        model.addAttribute("timer_d", timer_d);
        model.addAttribute("dagrate", dagrate);
        model.addAttribute("status", timer_d >= dagrate ? "OK" : "⚠Under dagrate");

        return "tasks/task-overview";
    }
}
