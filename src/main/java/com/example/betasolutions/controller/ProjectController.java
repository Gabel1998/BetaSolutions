package com.example.betasolutions.controller;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("project", new Project());
        return "projects/create"; //peger på create.html i projects-directory
    }

    @PostMapping ("/create")
    public String createProject(@ModelAttribute Project project, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }

        projectService.createProject(project);
        session.setAttribute("successMessage", "Projekt er blevet oprettet");
        return "redirect:/projects"; //redirects til list.html efter oprettelse
    }

    @GetMapping ("edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/auth/login";
        }

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

}
