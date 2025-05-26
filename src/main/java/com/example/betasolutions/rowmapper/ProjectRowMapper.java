package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.Project;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProjectRowMapper implements RowMapper<Project> {

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("p_id"));
        project.setName(rs.getString("p_name"));
        project.setDescription(rs.getString("p_description"));
        // READ p_start_date and p_end_date as java.sql.Date (may be null in DB)
        Date sqlStart = rs.getDate("p_start_date");
        Date sqlEnd   = rs.getDate("p_end_date");

        // ONLY convert to LocalDate if non-null, otherwise leave null
        LocalDate start = (sqlStart != null ? sqlStart.toLocalDate() : null);
        LocalDate end   = (sqlEnd   != null ? sqlEnd.toLocalDate()   : null);

        project.setStartDate(start);
        project.setEndDate(end);
        project.setCreatedAt(rs.getTimestamp("p_created_at").toLocalDateTime());
        project.setUpdatedAt(rs.getTimestamp("p_updated_at").toLocalDateTime());
        return project;
    }
}
