package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // CREATE
    @PostMapping
    public void addEmployee(@RequestBody Employees employee) {
        employeeService.addEmployee(employee);
    }

    // READ ALL
    @GetMapping
    public List<Employees> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public Employees getEmployeeById(@PathVariable Integer emId) {
        return employeeService.getEmployeeById(emId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public void updateEmployee(@PathVariable Integer emId, @RequestBody Employees employee) {
        employee.setEmId(emId);
        employeeService.updateEmployee(employee);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Integer emId) {
        employeeService.deleteEmployee(emId);
    }
}
