package com.example.betasolutions.repository;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.rowmapper.SubProjectRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for at arbejde med SubProject-data via JDBC
@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Gemmer et nyt SubProject i databasen
    public void save(SubProject subProject) {
        String sql = "INSERT INTO tb_subprojects (sp_p_id, sp_name, sp_description, sp_start_date, sp_end_date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, subProject.getProjectId(), subProject.getName(), subProject.getDescription(), subProject.getStartDate(), subProject.getEndDate());
    }

    // Henter alle SubProjects
    public List<SubProject> findAll() {
        String sql = "SELECT * FROM tb_subprojects";
        return jdbcTemplate.query(sql, new SubProjectRowMapper());
    }

    // Finder et SubProject på ID
    public Optional<SubProject> findById(Integer id) {
        String sql = "SELECT * FROM tb_subprojects WHERE sp_id = ?";
        return jdbcTemplate.query(sql, new SubProjectRowMapper(), id)
                .stream()
                .findFirst();
    }

    // Opdaterer et SubProject
    public void update(SubProject subProject) {
        String sql = "UPDATE tb_subprojects SET sp_p_id = ?, sp_name = ?, sp_description = ?, sp_start_date = ?, sp_end_date = ? WHERE sp_id = ?";
        jdbcTemplate.update(sql, subProject.getProjectId(), subProject.getName(), subProject.getDescription(), subProject.getStartDate(), subProject.getEndDate(), subProject.getId());
    }

    // Sletter et SubProject
    public void delete(Integer id) {
        String sql = "DELETE FROM tb_subprojects WHERE sp_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Henter alle SubProjects for et givet Project ID
    public Optional<SubProject> findById(int subProjectId) {
        String sql = "SELECT * FROM tb_subprojects WHERE sp_id = ?";
        return jdbcTemplate.query(sql, new SubProjectRowMapper(), subProjectId)
                .stream()
                .findFirst();
    }
}
