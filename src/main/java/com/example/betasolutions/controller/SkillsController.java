package com.example.betasolutions.controller;

import com.example.betasolutions.model.Skills;
import com.example.betasolutions.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/skills")
    public class SkillsController {

        @Autowired
        private SkillsService skillsService;

        @PostMapping
        public void addSkill(@RequestBody Skills skills) {
            skillsService.addSkill(skills);
        }

        @GetMapping
        public List<Skills> getAllSkills() {
            return skillsService.getAllSkills();
        }

        @GetMapping("/{id}")
        public Skills getSkillById(@PathVariable Integer skId) {
            return skillsService.getSkillById(skId);
        }

        @PutMapping("/{id}")
        public void updateSkill(@PathVariable Integer skId, @RequestBody Skills skills) {
            skills.setId(skId);
            skillsService.updateSkill(skills);
        }

        @DeleteMapping("/{id}")
        public void deleteSkill(@PathVariable Integer skId) {
            skillsService.deleteSkill(skId);
        }
    }