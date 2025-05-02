package com.example.betasolutions.controller;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);

        boolean overLimit = taskService.isDailyHoursExceeded(tasks, 8.0); // 8 timer er bare et eksempel. Ved ikke hvad det skal være
        model.addAttribute("overLimit", overLimit);

        return "tasks/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("pageTitle", "Opret task");
        model.addAttribute("task", new Task());
        return "tasks/create";
    }


    @PostMapping("/create")
    public String createTask(@ModelAttribute @Valid Task task, BindingResult result, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            return "tasks/create";
        }

        taskService.createTask(task);
        return "redirect:/tasks";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("pageTitle", "Rediger task");
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

        return "tasks/list";
    }

    @GetMapping("/task/{id}/distribution")
    public String getDailyDistribution(@PathVariable("id") Long taskId, Model model) {
        double dailyHours = taskService.getTotalDailyHoursForTask(taskId);
        model.addAttribute("dailyHours", dailyHours);
        return "tasks/distribution"; /// ja, eller hvad det nu skal være, nu har jeg lavet en midlertidig
    }

}
