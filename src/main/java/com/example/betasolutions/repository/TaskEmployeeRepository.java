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

    public List<TaskEmployee> findAll() {
        String sql = "SELECT * FROM tb_task_employees";
        return jdbcTemplate.query(sql, new TaskEmployeeRowMapper());
    }


    public List<TaskEmployee> findByTaskId(Long taskId) {
        String sql = "SELECT * FROM tb_task_employees WHERE tse_ts_id = ?";
        return jdbcTemplate.query(sql, new TaskEmployeeRowMapper(), taskId);
    }


    public List<String> findAssignedEmployeeNamesByTaskId(Integer taskId) {
        String sql = "SELECT e.em_first_name FROM tb_task_employees te " +
                     "JOIN tb_employees e ON te.tse_e_id = e.em_id " +
                     "WHERE te.tse_ts_id = ?";
                return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("em_first_name"), taskId);

        /// hardcoded mock for testing
        //return List.of("Alexander", "Andreas", "Rasmus", "Sofie");
    }


}
