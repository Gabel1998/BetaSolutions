package com.example.betasolutions.controller;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.service.SubProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subprojects")
public class SubProjectController {

    private final SubProjectService subProjectService;

    public SubProjectController(SubProjectService subProjectService) {
        this.subProjectService = subProjectService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    @GetMapping
    public String listSubProjects(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("pageTitle", "Sub Projects");
        model.addAttribute("subProjects", subProjectService.getAllSubProjects());
        return "subprojects/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        model.addAttribute("pageTitle", "Opret subprojekt");
        model.addAttribute("subProject", new SubProject());
        return "subprojects/create";
    }

    @PostMapping("/create")
    public String createSubProject(@ModelAttribute SubProject subProject, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        subProjectService.createSubProject(subProject);
        return "redirect:/subprojects";
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

    @PostMapping("/update/{id}")
    public String updateSubProject(@PathVariable Integer id, @ModelAttribute SubProject subProject, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        subProject.setId(id);
        subProjectService.updateSubProject(subProject);
        return "redirect:/subprojects";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubProject(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        subProjectService.deleteSubProject(id);
        return "redirect:/subprojects";
    }
}
