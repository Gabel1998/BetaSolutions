package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import com.example.betasolutions.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("SpringViewInspection")
/**
 * Handles primary navigation and information pages.
 */
@Controller
public class HomeController {

    private final ProjectService projectService;
    private final EmployeeService employeeService;

    // Constructor injection
    @Autowired
    public HomeController(ProjectService projectService, EmployeeService employeeService) {
        this.projectService = projectService;
        this.employeeService = employeeService;
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

    // Check if user is logged in
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // Display user settings form
    @GetMapping("/users/settings")
    public String showUserSettings(
            @RequestParam(required = false) Integer employeeId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        // Get current logged in user
        String username = (String) session.getAttribute("user");
        Optional<Employees> currentUserOptional = employeeService.findByUsername(username);

        if (!currentUserOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
            return "redirect:/";
        }

        // Get all employees for dropdown
        List<Employees> allEmployees = employeeService.getAllEmployees();
        model.addAttribute("allEmployees", allEmployees);

        // Determine which employee to edit
        Employees employeeToEdit;

        if (employeeId != null) {
            Optional<Employees> selectedEmployeeOptional = employeeService.getEmployeeById(employeeId);
            if (selectedEmployeeOptional.isPresent()) {
                employeeToEdit = selectedEmployeeOptional.get();
            } else {
                employeeToEdit = currentUserOptional.get();
                redirectAttributes.addFlashAttribute("errorMessage", "Selected employee not found");
            }
        } else {
            employeeToEdit = currentUserOptional.get(); // default to current user
        }

        model.addAttribute("employee", employeeToEdit);
        model.addAttribute("pageTitle", "User Settings");
        return "users/settings";
    }

    // Update user profile
    @PostMapping("/users/update")
    public String updateUserProfile(
            @RequestParam Integer employeeId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String password,
            @RequestParam Double emEfficiency,
            @RequestParam Double maxWeeklyHours,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }


        Optional<Employees> employeeOptional = employeeService.getEmployeeById(employeeId);

        if (employeeOptional.isPresent()) {
            Employees employee = employeeOptional.get();


            employee.setEmFirstName(firstName);
            employee.setEmLastName(lastName);
            employee.setEmEfficiency(emEfficiency);
            employee.setMaxWeeklyHours(maxWeeklyHours);

            // Only update password if provided
            if (password != null && !password.trim().isEmpty()) {
                employee.setEmPassword(password);
            }

            try {
                employeeService.updateEmployee(employee);
                redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error updating employee: " + e.getMessage());
            }

            return "redirect:/users/settings?employeeId=" + employeeId;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found");
            return "redirect:/users/settings";
        }
    }
}
