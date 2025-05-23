package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Manages user authentication, registration and session handling.
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Show login screen
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "login"; // login.html
    }

    // End user session and redirect to login
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    // Show registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "register";
    }

    // Create new user account
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               HttpSession session) {

        if (firstName != null) {
            Employees newUser = new Employees();
            newUser.setEmUsername(username);
            newUser.setEmPassword(password); // TODO: hash password!
            newUser.setEmFirstName(firstName);
            newUser.setEmLastName(lastName);
            newUser.setEmEfficiency(1.0);
            newUser.setMaxWeeklyHours(40);

            employeeRepository.registerNewUser(newUser);
            return "redirect:/auth/login";
        } else {
            session.setAttribute("user", username);
            return "redirect:/projects";
        }
    }

    // Authenticate user credentials
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {

        Optional<Employees> employeeOptional = employeeRepository.findByUsername(username);

        if (employeeOptional.isPresent() && employeeOptional.get().getEmPassword().equals(password)) {
            session.setAttribute("user", username);
            return "redirect:/projects";
        } else {
            session.setAttribute("errorMessage", "Invalid credentials");
            return "redirect:/auth/login";
        }
    }
}