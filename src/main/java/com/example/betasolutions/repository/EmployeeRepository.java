package com.example.betasolutions.repository;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.rowmapper.EmployeeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeRepository(@Qualifier("employeesJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE
    public void addEmployee(Employees employee) {
        String sql = "INSERT INTO tb_employees (em_first_name, em_last_name, em_efficiency, em_created_at, em_updated_at, max_weekly_hours) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                employee.getEmFirstName(),
                employee.getEmLastName(),
                employee.getEmEfficiency(),
                employee.getEmCreatedAt(),
                employee.getEmUpdatedAt(),
                employee.getMaxWeeklyHours());
    }

    // READ ALL
    public List<Employees> getAllEmployees() {
        String sql = "SELECT * FROM tb_employees";
        return jdbcTemplate.query(sql, new EmployeeRowMapper());
    }

    // READ BY ID
    public Employees getEmployeeById(long id) {
        String sql = "SELECT * FROM tb_employees WHERE em_id = ?";
        List<Employees> result = jdbcTemplate.query(sql, new EmployeeRowMapper(), id);
        if (result.isEmpty()) {
            System.out.println("⚠️ Medarbejder med ID " + id + " ikke fundet i tb_employees.");
            return null;
        }
        return result.get(0);
    }


    // UPDATE
    public void updateEmployee(Employees employee) {
        String sql = "UPDATE tb_employees SET em_first_name = ?, em_last_name = ?, em_efficiency = ?, em_updated_at = ?, max_weekly_hours = ? WHERE em_id = ?";
        jdbcTemplate.update(sql,
                employee.getEmFirstName(),
                employee.getEmLastName(),
                employee.getEmEfficiency(),
                employee.getEmUpdatedAt(),
                employee.getMaxWeeklyHours(),
                employee.getEmId());
    }

    // DELETE
    public void deleteEmployee(int id) {
        String sql = "DELETE FROM tb_employees WHERE em_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // READ MAXIMUM WEEKLY HOURS
    public double getMaxWeeklyHours(long id) {
        String sql = "SELECT max_weekly_hours FROM tb_employees WHERE em_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, id);
    }

    // UPDATE MAXIMUM WEEKLY HOURS
    public void updateMaxWeeklyHours(long id, double maxWeeklyHours) {
        String sql = "UPDATE tb_employees SET max_weekly_hours = ? WHERE em_id = ?";
        jdbcTemplate.update(sql, maxWeeklyHours, id);
    }
}