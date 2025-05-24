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

    // CREATE
    public void save(Task task) {
        String sql = "INSERT INTO tb_tasks (ts_sp_id, ts_name, ts_description, ts_estimated_hours, ts_actual_hours, start_date, end_date, project_id, ts_created_at, ts_updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                task.getSubProjectId(),
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getStartDate(),
                task.getEndDate(),
                task.getProjectId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    // UPDATE
    public void update(Task task) {
        String sql = "UPDATE tb_tasks SET ts_sp_id = ?, ts_name = ?, ts_description = ?, ts_estimated_hours = ?, ts_actual_hours = ?, start_date = ?, end_date = ?, project_id = ?, ts_updated_at = ? WHERE ts_id = ?";
        jdbcTemplate.update(sql,
                task.getSubProjectId(),
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getStartDate(),
                task.getEndDate(),
                task.getProjectId(),
                task.getUpdatedAt(),
                task.getId()
        );
    }


    // READ ALL
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

    // Fetch all Tasks for a given SubProject ID
    public List<Task> findBySubProjectId(int subProjectId) {
        String sql = "SELECT * FROM tb_tasks WHERE ts_sp_id = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), subProjectId);
    }

    // DELETE
    public void delete(Long id) {
        try {
            // First delete related records in tb_tasks_resources
            String deleteResourcesSql = "DELETE FROM tb_tasks_resources WHERE tsre_ts_id = ?";
            jdbcTemplate.update(deleteResourcesSql, id);

            // Then delete related records in tb_task_employees
            String deleteEmployeesSql = "DELETE FROM tb_task_employees WHERE tse_ts_id = ?";
            jdbcTemplate.update(deleteEmployeesSql, id);

            // Check if tb_task_logs table exists before trying to delete from it
            try {
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'tb_task_logs'", Integer.class);


                String deleteLogsSql = "DELETE FROM tb_task_logs WHERE log_ts_id = ?";
                jdbcTemplate.update(deleteLogsSql, id);
            } catch (Exception e) {
            }

            // Finally delete the task itself
            String sql = "DELETE FROM tb_tasks WHERE ts_id = ?";
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw e; // re-throw to let service layer handle it
        }
    }
}
