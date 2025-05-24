package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller for user settings and profile management
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private EmployeeService employeeService;

    // Check if user is logged in
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // Display user settings form
    @GetMapping("/settings")
    public String showUserSettings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        String username = (String) session.getAttribute("user");
        Optional<Employees> employeeOptional = employeeService.findByUsername(username);

        if (employeeOptional.isPresent()) {
            model.addAttribute("employee", employeeOptional.get());
            model.addAttribute("pageTitle", "User Settings");
            return "users/settings";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
            return "redirect:/";
        }
    }

    // Update user profile
    @PostMapping("/update")
    public String updateUserProfile(
            @RequestParam(required = false) Integer userId,
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

        String username = (String) session.getAttribute("user");
        Optional<Employees> employeeOptional = employeeService.findByUsername(username);

        if (employeeOptional.isPresent()) {
            Employees employee = employeeOptional.get();

            // Update employee fields
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
                redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            }

            return "redirect:/users/settings";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
            return "redirect:/";
        }
    }
}
