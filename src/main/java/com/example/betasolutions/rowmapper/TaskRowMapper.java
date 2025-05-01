package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.Task;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException{
        Task task = new Task();
        task.setId(rs.getLong("ts_id")); //ID fra databasen
        task.setSubProjectId(rs.getInt("ts_sp_id")); //Reference til subProject
        task.setProjectId(rs.getLong("ts_pj_id")); //Reference til project
        task.setName(rs.getString("ts_name"));
        task.setDescription(rs.getString("ts_description"));
        task.setEstimatedHours(rs.getDouble("ts_estimated_hours"));
        task.setActualHours(rs.getDouble("ts_actual_hours"));
        task.setCreatedAt(rs.getTimestamp("ts_created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("ts_updated_at").toLocalDateTime());

        return task;
    }
}
