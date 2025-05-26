package com.example.betasolutions.repository;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.rowmapper.ProjectRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE
    public void save(Project project) {
        String sql = "INSERT INTO tb_projects (p_name, p_description, p_start_date, p_end_date) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate());

        // Create a KeyHolder to capture the generated key
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            // Use a PreparedStatement to insert the project and retrieve the generated key
            PreparedStatement ps = conn.prepareStatement(sql, new String[] {"p_id"});
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setObject(3, project.getStartDate());
            ps.setObject(4, project.getEndDate());
            return ps;
        }, keyHolder);

        // Its important to check if the key is not null before setting it
        Number key = keyHolder.getKey();
        if (key != null) {
            project.setId((int) key.longValue());
        }
    }

    // READ ALL
    public List<Project> findAll() {
        String sql = "SELECT * FROM tb_projects";
        return jdbcTemplate.query(sql, new ProjectRowMapper());

    }

    //    Optional means: "There may be something. But there could also be nothing." Helps with avoiding nullPointerExceptions
    public Optional<Project> findById(long id) {
        String sql = "SELECT * FROM tb_projects WHERE p_id = ?";
        return jdbcTemplate.query(sql, new ProjectRowMapper(), id)
                .stream()
                .findFirst();
    }

    // UPDATE
    public void update(Project project) {
        String sql = "UPDATE tb_projects SET p_name = ?, p_description = ?, p_start_date = ?, p_end_date = ? WHERE p_id = ?";
        jdbcTemplate.update(sql, project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate(), project.getId());
    }

    // DELETE
    public void delete(Integer id) {
        String sql = "DELETE FROM tb_projects WHERE p_id = ?";
        jdbcTemplate.update(sql, id);
    }

}
