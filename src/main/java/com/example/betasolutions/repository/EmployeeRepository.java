package com.example.betasolutions.repository;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.rowmapper.EmployeeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CREATE
    public void addEmployee(Employees employee) {
        String sql = "INSERT INTO employees (em_first_name, em_last_name, em_efficiency, em_created_at, em_updated_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                employee.getEmFirstName(),
                employee.getEmLastName(),
                employee.getEmEfficiency(),
                employee.getEmCreatedAt(),
                employee.getEmUpdatedAt());
    }

    // READ ALL
    public List<Employees> getAllEmployees() {
        String sql = "SELECT * FROM employees";
        return jdbcTemplate.query(sql, new EmployeeRowMapper());
    }

    // READ BY ID
    public Employees getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE em_id = ?";
        return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(), id);
    }

    // UPDATE
    public void updateEmployee(Employees employee) {
        String sql = "UPDATE employees SET em_first_name = ?, em_last_name = ?, em_efficiency = ?, em_updated_at = ? WHERE em_id = ?";
        jdbcTemplate.update(sql,
                employee.getEmFirstName(),
                employee.getEmLastName(),
                employee.getEmEfficiency(),
                employee.getEmUpdatedAt(),
                employee.getEmId());
    }

    // DELETE
    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE em_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // READ MAXIMUM WEEKLY HOURS
    public double getMaxWeeklyHours(int id) {
        String sql = "SELECT em_max_weekly_hours FROM employees WHERE em_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, id);
    }

    // UPDATE MAXIMUM WEEKLY HOURS
    public void updateMaxWeeklyHours(int id, double maxWeeklyHours) {
        String sql = "UPDATE employees SET em_max_weekly_hours = ? WHERE em_id = ?";
        jdbcTemplate.update(sql, maxWeeklyHours, id);
    }
}
