package com.example.betasolutions.repository;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.rowmapper.TaskRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository to work with Task data via JDBC
@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Save new Task in database
    public void save(Task task) {
        String sql = "INSERT INTO tb_tasks (ts_sp_id, ts_name, ts_description, ts_estimated_hours, ts_actual_hours, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                task.getSubProjectId(),
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getStartDate(),
                task.getEndDate());
    }

    public void update(Task task) {
        String sql = "UPDATE tb_tasks SET ts_sp_id = ?, ts_name = ?, ts_description = ?, ts_estimated_hours = ?, ts_actual_hours = ?, start_date = ?, end_date = ? WHERE ts_id = ?";
        jdbcTemplate.update(sql,
                task.getSubProjectId(),
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getStartDate(),
                task.getEndDate(),
                task.getId());
    }


    // Fetch all Tasks
    public List<Task> findAll() {
        String sql = "SELECT * FROM tb_tasks";
        return jdbcTemplate.query(sql, new TaskRowMapper());
    }

    // Find Task on ID
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tb_tasks WHERE ts_id = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), id)
                .stream()
                .findFirst();
    }

    // Delete task
    public void delete(Long id) {
        // First delete related records in tb_task_employees
        String deleteRelatedSql = "DELETE FROM tb_task_employees WHERE tse_ts_id = ?";
        jdbcTemplate.update(deleteRelatedSql, id);

        // Then delete the task itself
        String sql = "DELETE FROM tb_tasks WHERE ts_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Fetch all Tasks for a given SubProject ID
    public List<Task> findBySubProjectId(int subProjectId) {
        String sql = "SELECT * FROM tb_tasks WHERE ts_sp_id = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), subProjectId);
    }
}
