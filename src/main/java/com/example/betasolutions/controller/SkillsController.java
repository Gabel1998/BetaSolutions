package com.example.betasolutions.controller;

import com.example.betasolutions.model.Skills;
import com.example.betasolutions.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API for managing skill definitions.
 */
@RestController
@RequestMapping("/skills")
public class SkillsController {

    @Autowired
    private SkillsService skillsService;

    // Create new skill
    @PostMapping
    public void addSkill(@RequestBody Skills skills) {
        skillsService.addSkill(skills);
    }

    // Get all defined skills
    @GetMapping
    public List<Skills> getAllSkills() {
        return skillsService.getAllSkills();
    }

    // Get skill by ID
    @GetMapping("/{id}")
    public Skills getSkillById(@PathVariable("id") Integer skId) {
        return skillsService.getSkillById(skId);
    }

    // Update existing skill
    @PutMapping("/{id}")
    public void updateSkill(@PathVariable("id") Integer skId, @RequestBody Skills skills) {
        skills.setId(skId);
        skillsService.updateSkill(skills);
    }

    // Delete a skill
    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable("id") Integer skId) {
        skillsService.deleteSkill(skId);
    }
}