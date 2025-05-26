package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for managing employees - provides CRUD endpoints for employee data.
 */
@RestController
@SuppressWarnings("SpringViewInspection")
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Get all employees
    @GetMapping
    public List<Employees> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employees> getEmployeeById(@PathVariable("id") Integer emId) {
        return employeeService.getEmployeeById(emId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new employee
    @PostMapping
    public void addEmployee(@RequestBody Employees employee) {
        employeeService.addEmployee(employee);
    }

    // Update existing employee
    @PutMapping("/{id}")
    public void updateEmployee(@PathVariable("id") Integer emId, @RequestBody Employees employee) {
        employee.setEmId(emId);
        employeeService.updateEmployee(employee);
    }

    // Delete employee by ID
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable("id") Integer emId) {
        employeeService.deleteEmployee(emId);
    }
}