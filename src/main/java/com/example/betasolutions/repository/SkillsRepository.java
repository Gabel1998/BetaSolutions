package com.example.betasolutions.repository;

import com.example.betasolutions.model.Skills;
import com.example.betasolutions.rowmapper.SkillsRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkillsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //CREATE
    public void addSkill(Skills skills) {
        String sql = "INSERT INTO skills (sk_name) VALUES (?)";
        jdbcTemplate.update(sql, skills.getSkName());
    }
    //READ ALL
    public List<Skills> getAllSkills() {
        String sql = "SELECT * FROM skills";
        return jdbcTemplate.query(sql, new SkillsRowMapper());
    }
    //READ BY ID
    public Skills getSkillById(Integer skId) {
        String sql = "SELECT * FROM skills WHERE sk_id = ?";
        return jdbcTemplate.queryForObject(sql, new SkillsRowMapper(), skId);
    }
    //UPDATE
    public void updateSkill(Skills skills) {
        String sql = "UPDATE skills SET sk_name = ? WHERE sk_id = ?";
        jdbcTemplate.update(sql, skills.getSkName(), skills.getSkId());
    }
    //DELETE
    public void deleteSkill(Integer skId) {
        String sql = "DELETE FROM skills WHERE sk_id = ?";
        jdbcTemplate.update(sql, skId);
    }
}
