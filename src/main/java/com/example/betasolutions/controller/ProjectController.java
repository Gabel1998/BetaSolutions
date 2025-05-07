package com.example.betasolutions.controller;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //Kontrol om bruger er logget ind
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    @GetMapping
    public String listProjects(Model model, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }

        model.addAttribute("pageTitle", "Alle projekter");
        model.addAttribute("projects", projectService.getAllProjects());

        //hvis der er en succes i session
        String succesMessage = (String)session.getAttribute("successMessage");
        if (succesMessage != null) {
            model.addAttribute("successMessage", succesMessage);
            session.removeAttribute("successMessage");
        }
        return "projects/list"; //henviser til en list.html fil i templates/projects
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }
        model.addAttribute("pageTitle", "Opret projekt");
        model.addAttribute("project", new Project());
        return "projects/create"; //peger på create.html i projects-directory
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

    @GetMapping ("edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }
        model.addAttribute("pageTitle", "Rediger projekt");
        Project project = projectService.getProjectById(id)
                .orElseThrow(()-> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        return "projects/edit"; //henviser på edit.html i projects directory
    }

    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Integer id, @ModelAttribute Project project, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }

        project.setId(id);
        projectService.updateProject(project);
        session.setAttribute("successMessage", "Projekt er blevet opdateret");
        return "redirect:/projects";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }

        projectService.deleteProject(id);
        session.setAttribute("successMessage", "Projekt er blevet slettet");
        return "redirect:/projects";
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



}
