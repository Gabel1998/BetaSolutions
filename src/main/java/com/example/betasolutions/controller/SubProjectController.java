package com.example.betasolutions.controller;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.service.*;
import com.example.betasolutions.utils.DateUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages subproject operations and gantt chart visualization.
 */
@Controller
@RequestMapping("/subprojects")
public class SubProjectController {

    private final ProjectService projectService;
    protected final SubProjectService subProjectService;
    private final TaskService taskService;
    private final PlantUmlGanttService ganttService;

    // Constructor injection
    public SubProjectController(SubProjectService subProjectService, ProjectService projectService, TaskService taskService, PlantUmlGanttService ganttService) {
        this.subProjectService = subProjectService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.ganttService = ganttService;
    }

    // Verify authentication
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // List all subprojects for a project
    @GetMapping
    public String listSubProjects(@RequestParam(value = "projectId", required = false) Integer projectId,
                                  Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        if (projectId == null) return "redirect:/projects";
        //sort subprojects by start date
        List<SubProject> subProjects = subProjectService.getAllSubProjectsByProjectId(projectId)
                .stream()
                .sorted(Comparator.comparing(SubProject::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))) // sort by start date
                .toList();


        // calculate total task hours for each subproject
        List<Integer> subProjectIds = subProjects.stream()
                .map(SubProject::getId)
                .collect(Collectors.toList());
        Map<Integer, Map<String, Double>> subProjectHours = subProjectService.calculateTaskHoursBySubProjectIds(subProjectIds);


        model.addAttribute("subProjects", subProjects); // sorted list
        model.addAttribute("project", projectService.getProjectById(projectId)
                .orElseThrow(() -> new RuntimeException("Projekt ikke fundet")));
        model.addAttribute("pageTitle", "Subprojekter for projekt");
        model.addAttribute("subProjectHours", subProjectHours);

        // Handle success message if present in session
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        return "subprojects/list";
    }

    // Show form for creating a new subproject
    @GetMapping("/create")
    public String showCreateSubProjectForm(@RequestParam("projectId") Integer projectId, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        SubProject subProject = new SubProject();
        subProject.setProjectId(projectId);
        model.addAttribute("subProject", subProject);

        return "subprojects/create";
    }

    // Delete a subproject by ID
    @GetMapping("/delete/{id}")
    public String deleteSubProject(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        // Get project ID before deleting the subproject
        Integer projectId = subProjectService.getSubProjectById(id)
                .map(SubProject::getProjectId)
                .orElse(null);

        subProjectService.deleteSubProject(id);

        // Add success message to session
        session.setAttribute("successMessage", "Subproject deleted successfully");

        // Redirect to project's subprojects list if project ID is available, otherwise to projects list
        return projectId != null ? "redirect:/subprojects?projectId=" + projectId : "redirect:/projects";
    }

    // Show statistics and performance data for a subproject
    @GetMapping("/overview/{subProjectId}")
    public String showSubProjectOverview(@PathVariable int subProjectId, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        Map<String, Object> overview = subProjectService.calculateSubProjectOverview(subProjectId);
        model.addAllAttributes(overview);
        return "subprojects/overview";
    }

    // Show form for editing an existing subproject
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("pageTitle", "Rediger subprojekt");
        SubProject subProject = subProjectService.getSubProjectById(id)
                .orElseThrow(() -> new RuntimeException("SubProject not found"));
        model.addAttribute("subProject", subProject);
        return "subprojects/edit";
    }

    // Generate and download Gantt chart visualization
    @GetMapping("/{id}/gantt")
    public void generateGanttDiagram(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        SubProject subProject = subProjectService.getSubProjectById(id)
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        List<Task> tasks = taskService.getTasksBySubProjectId(id);
        byte[] imageBytes = ganttService.generateGantt(subProject, tasks); /// PlantUML needs byte[] to generate PNG picture

        String fileName = "gantt_" + subProject.getName().replaceAll("\\s+", "_") + "_" + id + "_" + LocalDate.now() + ".png"; /// filename when generating

        response.setContentType("image/png"); ///  content type = PNG
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName); /// sets header to download file
        response.getOutputStream().write(imageBytes); /// write image bytes to output stream
    }

    // Provide JSON data for front-end Gantt chart rendering
    @GetMapping("/api/subprojects/{id}/ganttdata")
    @ResponseBody
    public Map<String, Object> getGanttData(@PathVariable Integer id) {
        SubProject subProject = subProjectService.getSubProjectById(id)
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        List<Task> tasks = taskService.getTasksBySubProjectId(id);

        List<Map<String, Object>> taskData = tasks.stream().map(task -> {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", task.getId());
            taskMap.put("name", task.getName());
            taskMap.put("startDate", DateUtils.formatDate(task.getStartDate()));
            taskMap.put("endDate", DateUtils.formatDate(task.getEndDate()));

            taskMap.put("assignedEmployees", taskService.getAssignedEmployeeNames(task.getId().intValue()));
            return taskMap;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("subProjectName", subProject.getName());
        response.put("startDate", subProject.getStartDate());
        response.put("endDate", subProject.getEndDate());
        response.put("tasks", taskData);

        return response;
    }

    // Process form submission for new subproject
    @PostMapping("/create")
    public String createSubProject(@ModelAttribute @Valid SubProject subProject, BindingResult result, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        if (result.hasErrors()) return "subprojects/create";

        subProjectService.createSubProject(subProject);
        session.setAttribute("successMessage", "Subproject created successfully");
        return "redirect:/subprojects?projectId=" + subProject.getProjectId();
    }

    // Process form submission for updating subproject
    @PostMapping("/update/{id}")
    public String updateSubProject(@PathVariable Integer id, @ModelAttribute SubProject subProject, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        subProject.setId(id);
        subProjectService.updateSubProject(subProject);
        session.setAttribute("successMessage", "Subproject updated successfully");
        return "redirect:/subprojects?projectId=" + subProject.getProjectId();
    }
}
