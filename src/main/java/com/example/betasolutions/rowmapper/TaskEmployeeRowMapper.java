package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.TaskEmployee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskEmployeeRowMapper implements RowMapper<TaskEmployee> {
    @Override
    public TaskEmployee mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskEmployee employee = new TaskEmployee();
        employee.setTseId(rs.getLong("tse_id"));
        employee.setTaskId(rs.getLong("tse_ts_id"));
        employee.setEmployeeId(rs.getString("tse_em_id"));
        employee.setHoursWorked(rs.getDouble("tse_hours_worked"));
        return employee;
    }
}
