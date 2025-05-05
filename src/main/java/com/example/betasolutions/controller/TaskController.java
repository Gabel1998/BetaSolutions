package com.example.betasolutions.controller;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.SubProjectService;
import com.example.betasolutions.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final SubProjectService subProjectService;

    public TaskController(TaskService taskService, ProjectService projectService, SubProjectService subProjectService) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.subProjectService = subProjectService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    @GetMapping
    public String listTasks(@RequestParam(value = "subProjectId", required = false) Integer subProjectId, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        SubProject subProject = subProjectService.getSubProjectById(subProjectId)
                .orElseThrow(() -> new RuntimeException("Subprojekt ikke fundet")); //skal have runtime exception, ellers virker prjektet ikke.

        List<Task> tasks = taskService.getTasksBySubProjectId(subProjectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subProject", subProject);

        boolean overLimit = taskService.isDailyHoursExceeded(tasks, 8.0);
        model.addAttribute("overLimit", overLimit);

        return "tasks/list";
    }


    @GetMapping("/create")
    public String showCreateForm(@RequestParam("subProjectId") Integer subProjectId,
                                 Model model,
                                 HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        SubProject subProject = subProjectService.getSubProjectById(subProjectId)
                .orElseThrow(() -> new RuntimeException("Delprojektet blev ikke fundet."));

        Task task = new Task();
        task.setSubProjectId(subProjectId);
        task.setProjectId(Long.valueOf(subProject.getProjectId()));

        model.addAttribute("task", task);
        model.addAttribute("subProject", subProject);

        return "tasks/create";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute @Valid Task task,
                             BindingResult result,
                             Model model,
                             HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            return "tasks/create";
        }

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Ugyldigt delprojekt – kunne ikke findes."));

        task.setProjectId(Long.valueOf(subProject.getProjectId()));
        taskService.createTask(task);

        return "redirect:/tasks?subProjectId=" + task.getSubProjectId();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        model.addAttribute("task", task);
        model.addAttribute("subProject", subProject);

        return "tasks/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute @Valid Task task,
                             BindingResult result,
                             Model model,
                             HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        task.setId(id);

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        // Sørg for at projectId ALTID er sat
        task.setProjectId(Long.valueOf(subProject.getProjectId()));

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            model.addAttribute("subProject", subProject);
            return "tasks/edit";
        }

        taskService.updateTask(task);
        return "redirect:/tasks?subProjectId=" + task.getSubProjectId();
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
        model.addAttribute("status", timer_d >= dagrate ? "OK" : "⚠ Under dagrate: (" + dagrate + ") ⚠");

        return "tasks/list";
    }

    @GetMapping("/task/{id}/distribution")
    public String getDailyDistribution(@PathVariable("id") Long taskId, Model model) {
        double dailyHours = taskService.getTotalDailyHoursForTask(taskId);
        model.addAttribute("dailyHours", dailyHours);
        return "tasks/distribution"; /// ja, eller hvad det nu skal være, nu har jeg lavet en midlertidig
    }

}
