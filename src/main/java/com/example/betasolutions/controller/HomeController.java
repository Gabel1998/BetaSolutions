package com.example.betasolutions.controller;

import com.example.betasolutions.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProjectService projectService;

    public HomeController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "index";
    }
}
