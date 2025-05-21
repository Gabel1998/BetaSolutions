package com.example.betasolutions.repository;

import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.rowmapper.TaskEmployeeRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TaskEmployeeRepository {
    private final JdbcTemplate jdbcTemplate;
    private final EmployeeRepository employeeRepository;

    public TaskEmployeeRepository(JdbcTemplate jdbcTemplate, EmployeeRepository employeeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.employeeRepository = employeeRepository;
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
        // First get the employee IDs from the tasks database
        String sql = "SELECT tse_em_id FROM tb_task_employees WHERE tse_ts_id = ?";
        List<Long> employeeIds = jdbcTemplate.queryForList(sql, Long.class, taskId);

        // Then fetch the names from the employees database using the EmployeeRepository
        return employeeIds.stream()
                .map(employeeRepository::getEmployeeById)
                .filter(employee -> employee != null)
                .map(employee -> employee.getEmFirstName())
                .collect(Collectors.toList());
    }

    public TaskEmployee findById(Long id) {
        String sql = "SELECT * FROM tb_task_employees WHERE tse_id = ?";
        List<TaskEmployee> results = jdbcTemplate.query(sql, new TaskEmployeeRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(TaskEmployee taskEmployee) {
        String sql = "INSERT INTO tb_task_employees (tse_ts_id, tse_em_id, tse_hours_worked, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                taskEmployee.getTaskId(),
                taskEmployee.getEmployeeId(),
                taskEmployee.getHoursWorked(),
                taskEmployee.getStartDate(),
                taskEmployee.getEndDate());
    }

    public void update(TaskEmployee taskEmployee) {
        String sql = "UPDATE tb_task_employees SET tse_ts_id = ?, tse_em_id = ?, tse_hours_worked = ?, " +
                "start_date = ?, end_date = ? WHERE tse_id = ?";
        jdbcTemplate.update(sql,
                taskEmployee.getTaskId(),
                taskEmployee.getEmployeeId(),
                taskEmployee.getHoursWorked(),
                taskEmployee.getStartDate(),
                taskEmployee.getEndDate(),
                taskEmployee.getTseId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM tb_task_employees WHERE tse_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<TaskEmployee> findByProjectId(int projectId) {
        String sql = "SELECT te.* FROM tb_task_employees te " +
                "JOIN tb_tasks t ON te.tse_ts_id = t.ts_id " +
                "WHERE t.project_id = ?";
        return jdbcTemplate.query(sql, new TaskEmployeeRowMapper(), projectId);
    }

    public void logHours(long taskId, long employeeId, double hoursWorked) {
        String sql = "INSERT INTO tb_task_employees (tse_ts_id, tse_em_id, tse_hours_worked) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, taskId, employeeId, hoursWorked);
    }

    public double getLoggedHoursForTaskAndEmployee(long taskId, long employeeId) {
        String sql = "SELECT SUM(tse_hours_worked) FROM tb_task_employees WHERE tse_ts_id = ? AND tse_em_id = ?";
        Double result = jdbcTemplate.queryForObject(sql, Double.class, taskId, employeeId);
        return result != null ? result : 0.0;
    }
}
