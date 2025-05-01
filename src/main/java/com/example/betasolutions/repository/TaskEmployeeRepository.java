package com.example.betasolutions.repository;

import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.rowmapper.TaskEmployeeRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskEmployeeRepository {
    private final JdbcTemplate jdbcTemplate;

    public TaskEmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskEmployee> findByTaskId(Long taskId) {
        String sql = "SELECT * FROM tb_task_employee WHERE tse_ts_id = ?";
        return jdbcTemplate.query(sql, new TaskEmployeeRowMapper(), taskId);
    }
}
