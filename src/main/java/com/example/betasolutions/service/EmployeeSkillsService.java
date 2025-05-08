package com.example.betasolutions.service;

import com.example.betasolutions.model.EmployeeSkills;
import com.example.betasolutions.repository.EmployeeSkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeSkillsService {

    private final EmployeeSkillsRepository employeeSkillsRepository;

    // Kontruktør
    public EmployeeSkillsService(EmployeeSkillsRepository employeeSkillsRepository) {
        this.employeeSkillsRepository = employeeSkillsRepository;
    }

    // CREATE
    public void addEmployeeSkill(EmployeeSkills employeeSkill) {
        employeeSkillsRepository.addEmployeeSkill(employeeSkill);
    }

    // READ ALL
    public List<EmployeeSkills> getAllEmployeeSkills() {
        return employeeSkillsRepository.getAllEmployeeSkills();
    }

    // DELETE (En bestemt skill fraa employee)
    public void deleteEmployeeSkill(int emskEmId, int emskSkId) {
        employeeSkillsRepository.deleteEmployeeSkill(emskEmId, emskSkId);
    }
}
