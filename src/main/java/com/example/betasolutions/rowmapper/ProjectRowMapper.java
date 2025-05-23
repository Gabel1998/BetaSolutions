package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.Project;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectRowMapper implements RowMapper<Project> {

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("p_id"));
        project.setName(rs.getString("p_name"));
        project.setDescription(rs.getString("p_description"));
        project.setStartDate(rs.getDate("p_start_date").toLocalDate());
        project.setEndDate(rs.getDate("p_end_date").toLocalDate());
        project.setCreatedAt(rs.getTimestamp("p_created_at").toLocalDateTime());
        project.setUpdatedAt(rs.getTimestamp("p_updated_at").toLocalDateTime());
        return project;
    }
}
