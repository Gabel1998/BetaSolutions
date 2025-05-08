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


    /// STRUKTUR I FØLGE ALEKSANDER(PO): GET, POST, PUT, DELETE
    @GetMapping("/login")
    public String ShowLoginForm(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "login"; //henviser til login.html
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session) {
        //Midlertidigt login
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("username", username);
            return "redirect:/projects";
        } else {
            return "redirect:/auth/login?error";
        }
    }

}
