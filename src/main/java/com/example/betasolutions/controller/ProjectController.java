package com.example.betasolutions.controller;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.ResourceService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages project operations including listing, creation, and resource tracking.
 */
@Controller
@RequestMapping("/projects")
@SuppressWarnings("SpringViewInspection")
public class ProjectController {

    private final ProjectService projectService;
    private final ResourceService resourceService;

    @Autowired
    public ProjectController(ProjectService projectService, ResourceService resourceService) {
        this.projectService = projectService;
        this.resourceService = resourceService;
    }

    // Verify user authentication
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // Display list of all projects with hours and CO2 data
    @GetMapping
    public String listProjects(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        //sort projects by start date
        List<Project> projects = projectService.getAllProjects()
                .stream()
                .sorted(Comparator.comparing(Project::getStartDate))
                .collect(Collectors.toList());


        List<Integer> projectIds = projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());

        Map<Integer, Map<String, Double>> projectHours = projectService.calculateProjectHoursByProjectIds(projectIds);

        Map<Integer, Double> projectCo2 = new HashMap<>();
        for (Project project : projects) {
            double co2 = resourceService.calculateTotalCo2ForProject(project.getId());
            if (co2 > 0) {
                projectCo2.put(project.getId(), co2); // Only valid values
            } else {
                projectCo2.put(project.getId(), -1.0); // Mark as n/a
            }
        }

        Map<Integer, Double> adjustedHours = new HashMap<>();
        for (Project project : projects) {
            double adjusted = projectService.adjustEstimatedHoursBasedOnEfficiency(project.getId());
            adjustedHours.put(project.getId(), adjusted);
        }

        model.addAttribute("pageTitle", "All Projects");
        model.addAttribute("projects", projects);
        model.addAttribute("projectHours", projectHours);
        model.addAttribute("projectCo2", projectCo2);
        model.addAttribute("adjustedHours", adjustedHours);

        String succesMessage = (String) session.getAttribute("successMessage");
        if (succesMessage != null) {
            model.addAttribute("successMessage", succesMessage);
            session.removeAttribute("successMessage");
        }
        return "projects/list";
    }

    // Show form for creating a new project
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("pageTitle", "Create Project");
        model.addAttribute("project", new Project());
        return "projects/create";
    }

    // Display project summary with actual, estimated and adjusted hours
    @GetMapping("/summary")
    public String showProjectSummary(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        List<Project> projects = projectService.getAllProjects();

        Map<Integer, Double> projectActualHoursMap = new HashMap<>();
        Map<Integer, Double> projectEstimatedHoursMap = new HashMap<>();
        Map<Integer, Double> adjustedHours = new HashMap<>();

        for (Project p : projects) {
            double actual = projectService.getTotalActualHoursForProject(p.getId());
            double estimated = projectService.getTotalEstimatedHoursForProject(p.getId());
            double adjusted = projectService.adjustEstimatedHoursBasedOnEfficiency(p.getId());

            projectActualHoursMap.put(p.getId(), actual);
            projectEstimatedHoursMap.put(p.getId(), estimated);
            adjustedHours.put(p.getId(), adjusted);
        }

        model.addAttribute("projects", projects);
        model.addAttribute("projectHoursMap", projectActualHoursMap);
        model.addAttribute("projectEstimatedMap", projectEstimatedHoursMap);
        model.addAttribute("adjustedHours", adjustedHours);
        return "projects/summary";
    }

    // Show form for editing an existing project
    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("pageTitle", "Edit Project");
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        return "projects/edit";
    }

    // Delete project and redirect to projects list
    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        projectService.deleteProject(id);
        session.setAttribute("successMessage", "Project deleted successfully");
        return "redirect:/projects";
    }

    // Process form submission for new project
    @PostMapping("/create")
    public String createProject(@ModelAttribute @Valid Project project, BindingResult result, HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Create Project");
            return "projects/create";
        }

        projectService.createProject(project);
        session.setAttribute("successMessage", "Project created successfully");
        return "redirect:/projects";
    }

    // Process form submission for updating project
    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Integer id, @ModelAttribute Project project, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        project.setId(id);
        projectService.updateProject(project);
        session.setAttribute("successMessage", "The project has been updated");
        return "redirect:/projects";
    }

}
