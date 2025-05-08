package com.example.betasolutions.controller;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.SubProjectService;
import com.example.betasolutions.utils.DateUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/subprojects")
public class SubProjectController {

    private final ProjectService projectService;
    protected final SubProjectService subProjectService;

    public SubProjectController(SubProjectService subProjectService, ProjectService projectService) {
        this.subProjectService = subProjectService;
        this.projectService = projectService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }


    /// STRUKTUR I FØLGE ALEKSANDER(PO): GET, POST, PUT, DELETE
    @GetMapping
    public String listSubProjects(@RequestParam(value = "projectId", required = false) Integer projectId,
                                  Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        if (projectId == null) return "redirect:/projects";

        List<SubProject> subProjects = subProjectService.getAllSubProjectsByProjectId(projectId);

        // Beregning af total timer af tasks for subprojekter
        List<Integer> subProjectIds = subProjects.stream()
                .map(SubProject::getId)
                .collect(Collectors.toList());
        Map<Integer, Map<String, Double>> subProjectHours = subProjectService.calculateTaskHoursBySubProjectIds(subProjectIds);


        model.addAttribute("subProjects", subProjectService.getAllSubProjectsByProjectId(projectId));
        model.addAttribute("project", projectService.getProjectById(projectId)
                .orElseThrow(() -> new RuntimeException("Projekt ikke fundet")));
        model.addAttribute("pageTitle", "Subprojekter for projekt");
        model.addAttribute("subProjectHours", subProjectHours);
        return "subprojects/list";
    }


    // Vis create form for subproject
    @GetMapping("/create")
    public String showCreateSubProjectForm(@RequestParam("projectId") Integer projectId, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        SubProject subProject = new SubProject();
        subProject.setProjectId(projectId);
        model.addAttribute("subProject", subProject);

        return "subprojects/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubProject(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        subProjectService.deleteSubProject(id);
        return "redirect:/subprojects";
    }

    @GetMapping("/overview/{subProjectId}")
    public String showSubProjectOverview(@PathVariable int subProjectId, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";

        Map<String, Object> overview = subProjectService.calculateSubProjectOverview(subProjectId);
        model.addAllAttributes(overview);
        return "subprojects/overview";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("pageTitle", "Rediger subprojekt");
        SubProject subProject = subProjectService.getSubProjectById(id)
                .orElseThrow(() -> new RuntimeException("SubProject not found"));
        model.addAttribute("subProject", subProject);
        return "subprojects/edit";
    }


    @PostMapping("/create")
    public String createSubProject(@ModelAttribute @Valid SubProject subProject, BindingResult result, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        if (result.hasErrors()) return "subprojects/create";

        subProjectService.createSubProject(subProject);
        session.setAttribute("successMessage", "Projekt er blevet oprettet");
        return "redirect:/subprojects?projectId=" + subProject.getProjectId();
    }

    @PostMapping("/update/{id}")
    public String updateSubProject(@PathVariable Integer id, @ModelAttribute SubProject subProject, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        subProject.setId(id);
        subProjectService.updateSubProject(subProject);
        return "redirect:/subprojects";
    }


}
