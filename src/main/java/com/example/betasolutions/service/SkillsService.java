package com.example.betasolutions.service;

import com.example.betasolutions.model.Skills;
import com.example.betasolutions.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillsService {

    private final SkillsRepository skillsRepository;

    // konstruktør
    public SkillsService(SkillsRepository skillsRepository) {
        this.skillsRepository = skillsRepository;
    }

    // CREATE
    public void addSkill(Skills skills) {
        skillsRepository.addSkill(skills);
    }

    // READ ALL
    public List<Skills> getAllSkills() {
        return skillsRepository.getAllSkills();
    }

    // READ BY ID
    public Skills getSkillById(int id) {
        return skillsRepository.getSkillById(id);
    }

    // UPDATE
    public void updateSkill(Skills skills) {
        skillsRepository.updateSkill(skills);
    }

    // DELETE
    public void deleteSkill(int id) {
        skillsRepository.deleteSkill(id);
    }
}
