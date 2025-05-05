package com.example.betasolutions.repository;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.rowmapper.TaskRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for at arbejde med Task-data via JDBC
@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Gemmer en ny Task i databasen
    public void save(Task task) {
        String sql = "INSERT INTO tb_tasks (ts_sp_id, ts_name, ts_description, ts_estimated_hours, ts_actual_hours) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, task.getSubProjectId(), task.getName(), task.getDescription(), task.getEstimatedHours(), task.getActualHours());
    }

    // Henter alle Tasks
    public List<Task> findAll() {
        String sql = "SELECT * FROM tb_tasks";
        return jdbcTemplate.query(sql, new TaskRowMapper());
    }

    // Finder en Task på ID
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tb_tasks WHERE ts_id = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), id)
                .stream()
                .findFirst();
    }

    // Opdaterer en Task
    public void update(Task task) {
        String sql = "UPDATE tb_tasks SET ts_sp_id = ?, ts_name = ?, ts_description = ?, ts_estimated_hours = ?, ts_actual_hours = ? WHERE ts_id = ?";
        jdbcTemplate.update(sql, task.getSubProjectId(), task.getName(), task.getDescription(), task.getEstimatedHours(), task.getActualHours(), task.getId());
    }

    // Sletter en Task
    public void delete(Long id) {
        String sql = "DELETE FROM tb_tasks WHERE ts_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Henter alle Tasks for et givet SubProject ID
    public List<Task> findBySubProjectId(int subProjectId) {
        String sql = "SELECT * FROM tb_tasks WHERE ts_sp_id = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), subProjectId);
    }
}
