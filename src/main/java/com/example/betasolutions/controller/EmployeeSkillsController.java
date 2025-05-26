package com.example.betasolutions.controller;

import com.example.betasolutions.model.EmployeeSkills;
import com.example.betasolutions.service.EmployeeSkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API for managing employee skill relationships.
 */
@RestController
@SuppressWarnings("SpringViewInspection")
@RequestMapping("/employee-skills")
public class EmployeeSkillsController {

    @Autowired
    private EmployeeSkillsService employeeSkillsService;

    // Create new skill assignment
    @PostMapping
    public void addEmployeeSkill(@RequestBody EmployeeSkills employeeSkill) {
        employeeSkillsService.addEmployeeSkill(employeeSkill);
    }

    // Get all employee skills
    @GetMapping
    public List<EmployeeSkills> getAllEmployeeSkills() {
        return employeeSkillsService.getAllEmployeeSkills();
    }

    // Remove skill from employee
    @DeleteMapping
    public void deleteEmployeeSkill(@RequestParam int emskEmId, @RequestParam int emskSkId) {
        employeeSkillsService.deleteEmployeeSkill(emskEmId, emskSkId);
    }
}