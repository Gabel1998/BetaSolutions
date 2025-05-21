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

@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private EmployeeRepository employeeRepository;

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

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "register";
    }

    // Handle registration
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

    // Handle login
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {

        Employees employee = employeeRepository.findByUsername(username);
        if (employee != null && employee.getEmPassword().equals(password)) {
            session.setAttribute("user", username);
            return "redirect:/projects";
        } else {
            session.setAttribute("errorMessage", "Invalid credentials");
            return "redirect:/auth/login";
        }
    }
}