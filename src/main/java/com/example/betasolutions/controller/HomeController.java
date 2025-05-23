package com.example.betasolutions.controller;

import com.example.betasolutions.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles primary navigation and information pages.
 */
@Controller
public class HomeController {

    private final ProjectService projectService;

    // Constructor injection
    public HomeController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Main landing page
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "index";
    }

    // Cookie policy page
    @GetMapping("/cookies")
    public String cookies() {
        return "cookies";
    }

    // Privacy policy page
    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

}