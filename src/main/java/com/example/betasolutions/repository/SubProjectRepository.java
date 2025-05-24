package com.example.betasolutions.repository;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.rowmapper.SubProjectRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for working with SubProject data via JDBC
@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE
    public void save(SubProject subProject) {
        String sql = "INSERT INTO tb_subprojects (sp_p_id, sp_name, sp_description, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, subProject.getProjectId(), subProject.getName(), subProject.getDescription(), subProject.getStartDate(), subProject.getEndDate());
    }

    // READ ALL
    public List<SubProject> findAll() {
        String sql = "SELECT * FROM tb_subprojects";
        return jdbcTemplate.query(sql, new SubProjectRowMapper());
    }

    // Find a SubProject by ID
    public Optional<SubProject> findById(Integer id) {
        String sql = "SELECT * FROM tb_subprojects WHERE sp_id = ?";
        return jdbcTemplate.query(sql, new SubProjectRowMapper(), id)
                .stream()
                .findFirst();
    }

    // Retrieve all SubProjects for a given Project ID
    public Optional<SubProject> findById(int subProjectId) {
        String sql = "SELECT * FROM tb_subprojects WHERE sp_id = ?";
        return jdbcTemplate.query(sql, new SubProjectRowMapper(), subProjectId)
                .stream()
                .findFirst();
    }

    public List<SubProject> findAllByProjectId(Integer projectId) {
        String sql = "SELECT * FROM tb_subprojects WHERE sp_p_id = ?";
        return jdbcTemplate.query(sql, new SubProjectRowMapper(), projectId);
    }

    // UPDATE
    public void update(SubProject subProject) {
        String sql = "UPDATE tb_subprojects SET sp_p_id = ?, sp_name = ?, sp_description = ?, start_date = ?, end_date = ? WHERE sp_id = ?";
        jdbcTemplate.update(sql, subProject.getProjectId(), subProject.getName(), subProject.getDescription(), subProject.getStartDate(), subProject.getEndDate(), subProject.getId());
    }

    // DELETE
    public void delete(Integer id) {
        String sql = "DELETE FROM tb_subprojects WHERE sp_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
