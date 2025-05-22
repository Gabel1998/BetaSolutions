package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.Task;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException{
        Task task = new Task();
        task.setId(rs.getLong("ts_id"));
        task.setSubProjectId(rs.getInt("ts_sp_id"));
        task.setName(rs.getString("ts_name"));
        task.setDescription(rs.getString("ts_description"));
        task.setEstimatedHours(rs.getDouble("ts_estimated_hours"));
        task.setActualHours(rs.getDouble("ts_actual_hours"));

        Date startDate = rs.getDate("start_date");
        if (startDate != null) task.setStartDate(startDate.toLocalDate());

        Date endDate = rs.getDate("end_date");
        if (endDate != null) task.setEndDate(endDate.toLocalDate());

        if (rs.getTimestamp("ts_created_at") != null)
            task.setCreatedAt(rs.getTimestamp("ts_created_at").toLocalDateTime());

        if (rs.getTimestamp("ts_updated_at") != null)
            task.setUpdatedAt(rs.getTimestamp("ts_updated_at").toLocalDateTime());

        task.setProjectId(rs.getLong("project_id"));
        return task;
    }
}
