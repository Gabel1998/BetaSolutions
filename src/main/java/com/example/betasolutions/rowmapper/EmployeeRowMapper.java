package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.Employees;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EmployeeRowMapper implements RowMapper<Employees> {

    @Override
    public Employees mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employees employees = new Employees();
        employees.setEmId(rs.getInt("em_id"));
        employees.setEmFirstName(rs.getString("em_first_name"));
        employees.setEmLastName(rs.getString("em_last_name"));
        employees.setEmEfficiency(rs.getDouble("em_efficiency"));
        employees.setMaxWeeklyHours(rs.getDouble("em_max_weekly_hours"));

        Timestamp created = rs.getTimestamp("em_created_at");
        if (created != null) {
            employees.setEmCreatedAt(created.toLocalDateTime());
        }

        Timestamp updated = rs.getTimestamp("em_updated_at");
        if (updated != null) {
            employees.setEmUpdatedAt(updated.toLocalDateTime());
        }

        return employees;
    }
}
