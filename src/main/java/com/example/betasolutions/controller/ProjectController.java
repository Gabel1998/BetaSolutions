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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ResourceService resourceService;

    @Autowired
    public ProjectController(ProjectService projectService, ResourceService resourceService) {
        this.projectService = projectService;
        this.resourceService = resourceService;
    }

    //Kontrol om bruger er logget ind
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping
    public String listProjects(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        List<Project> projects = projectService.getAllProjects();

        List<Integer> projectIds = projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());

        Map<Integer, Map<String, Double>> projectHours = projectService.calculateProjectHoursByProjectIds(projectIds);

        Map<Integer, Double> projectCo2 = new HashMap<>();
        for (Project project : projects) {
            double actualHours = 0.0;
            if (projectHours.containsKey(project.getId())) {
                actualHours = projectHours.get(project.getId()).getOrDefault("actualHours", 0.0);
            }
            double co2 = resourceService.calculateTotalCo2ForProject(project.getId(), actualHours);
            projectCo2.put(project.getId(), co2);
        }

        Map<Integer, Double> adjustedHours = new HashMap<>();
        for (Project project : projects) {
            double adjusted = projectService.adjustEstimatedHoursBasedOnEfficiency(project.getId());
            adjustedHours.put(project.getId(), adjusted);
        }

        model.addAttribute("pageTitle", "Alle projekter");
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

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("pageTitle", "Opret projekt");
        model.addAttribute("project", new Project());
        return "projects/create"; //peger på create.html i projects-directory
    }

    @GetMapping("/summary")
    public String showProjectSummary(Model model) {
        List<Project> projects = projectService.getAllProjects();

        Map<Integer, Double> projectActualHoursMap = new HashMap<>();
        Map<Integer, Double> projectEstimatedHoursMap = new HashMap<>();

        for (Project p : projects) {
            double actual = projectService.getTotalActualHoursForProject(p.getId());
            double estimated = projectService.getTotalEstimatedHoursForProject(p.getId());
            projectActualHoursMap.put(p.getId(), actual);
            projectEstimatedHoursMap.put(p.getId(), estimated);
        }

        model.addAttribute("projects", projects);
        model.addAttribute("projectHoursMap", projectActualHoursMap);
        model.addAttribute("projectEstimatedMap", projectEstimatedHoursMap);
        return "projects/summary";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("pageTitle", "Rediger projekt");
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        return "projects/edit"; //henviser på edit.html i projects directory
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        projectService.deleteProject(id);
        session.setAttribute("successMessage", "Projekt er blevet slettet");
        return "redirect:/projects";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute @Valid Project project, BindingResult result, HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Create Project");
            return "projects/create";
        }

        projectService.createProject(project);
        session.setAttribute("successMessage", "Project created successfully");
        return "redirect:/projects"; //Redirects til lists efter oprettelse
    }

    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Integer id, @ModelAttribute Project project, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        project.setId(id);
        projectService.updateProject(project);
        session.setAttribute("successMessage", "Projekt er blevet opdateret");
        return "redirect:/projects";
    }

    @GetMapping("/{id}/projects/list")
    public String getAdjustedEstimatedHours(@PathVariable int id, Model model) {
        double adjustedHours = projectService.adjustEstimatedHoursBasedOnEfficiency(id);
        model.addAttribute("adjustedHours", adjustedHours);
        return "projects/list";

    }
}
