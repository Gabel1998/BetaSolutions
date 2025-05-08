package com.example.betasolutions.controller;

import com.example.betasolutions.model.EmployeeSkills;
import com.example.betasolutions.repository.EmployeeSkillsRepository;
import com.example.betasolutions.service.EmployeeSkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-skills")
public class EmployeeSkillsController {

    @Autowired
    private EmployeeSkillsService employeeSkillsService;

    @PostMapping
    public void addEmployeeSkill(@RequestBody EmployeeSkills employeeSkill) {
        employeeSkillsService.addEmployeeSkill(employeeSkill);
    }

    @GetMapping
    public List<EmployeeSkills> getAllEmployeeSkills() {
        return employeeSkillsService.getAllEmployeeSkills();
    }

    @DeleteMapping
    public void deleteEmployeeSkill(@RequestParam int emskEmId, @RequestParam int emskSkId) {
        employeeSkillsService.deleteEmployeeSkill(emskEmId, emskSkId);
    }
}

