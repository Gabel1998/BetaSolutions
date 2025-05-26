package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.Employees;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employees> {

    @Override
    public Employees mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employees employees = new Employees();
        employees.setEmId(rs.getInt("em_id"));
        employees.setEmFirstName(rs.getString("em_first_name"));
        employees.setEmLastName(rs.getString("em_last_name"));
        employees.setEmUsername(rs.getString("em_username"));
        employees.setEmPassword(rs.getString("em_password"));
        employees.setEmEfficiency(rs.getDouble("em_efficiency"));
        employees.setEmCreatedAt(rs.getTimestamp("em_created_at").toLocalDateTime());
        employees.setEmUpdatedAt(rs.getTimestamp("em_updated_at").toLocalDateTime());
        employees.setMaxWeeklyHours(rs.getDouble("em_max_weekly_hours"));
        return employees;
    }
}
