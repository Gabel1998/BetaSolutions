package com.example.betasolutions.repository;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.rowmapper.ProjectRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Project project) {
        String sql = "INSERT INTO tb_projects (p_name, p_description, p_start_date, p_end_date) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate());
    }

    public List<Project> findAll() {
        String sql = "SELECT * FROM tb_projects";
        return jdbcTemplate.query(sql, new ProjectRowMapper());

    }

//    Optional betyder: "Der kan være noget. Men der kan også være ingenting." Hjælper med nullPointerExceptions
    public Optional<Project> findById(int id) {
        String sql = "SELECT * FROM tb_projects WHERE p_id = ?";
        return jdbcTemplate.query(sql, new ProjectRowMapper(), id)
                .stream()
                .findFirst();
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM tb_projects WHERE p_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void update(Project project) {
        String sql = "UPDATE tb_projects SET p_name = ?, p_description = ?, p_start_date = ?, p_end_date = ? WHERE p_id = ?";
        jdbcTemplate.update(sql, project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate(), project.getId());
    }
}
