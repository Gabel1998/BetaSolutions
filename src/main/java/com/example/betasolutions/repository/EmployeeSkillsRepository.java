package com.example.betasolutions.repository;

import com.example.betasolutions.model.EmployeeSkills;
import com.example.betasolutions.rowmapper.EmployeeSkillsRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeSkillsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CREATE
    public void addEmployeeSkill(EmployeeSkills employeeSkill) {
        String sql = "INSERT INTO employees_skills (emsk_em_id, emsk_sk_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, employeeSkill.getEmskEmId(), employeeSkill.getEmskSkId());
    }

    // READ ALL
    public List<EmployeeSkills> getAllEmployeeSkills() {
        String sql = "SELECT * FROM employees_skills";
        return jdbcTemplate.query(sql, new EmployeeSkillsRowMapper());
    }

    // DELETE
    public void deleteEmployeeSkill(int employeeId, int skillId) {
        String sql = "DELETE FROM employees_skills WHERE emsk_em_id = ? AND emsk_sk_id = ?";
        jdbcTemplate.update(sql, employeeId, skillId);
    }
}

