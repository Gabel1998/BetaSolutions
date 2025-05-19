package com.example.betasolutions.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class LoginController {

    // Display login page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "login"; // login.html
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    // Handle login form submission
    /// skal fikses så når man kan oprette bruger, den kan logge ind
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session) {
        // TEMPORARY: Only allow admin/admin123 for now
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("user", username);  // Store username in session
            return "redirect:/projects";
        } else {
            return "redirect:/auth/login?error";     // Show error on wrong login
        }
    }

}
