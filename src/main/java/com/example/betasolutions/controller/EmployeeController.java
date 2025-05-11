package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    /// STRUKTUR I FØLGE ALEKSANDER(PO): GET, POST, PUT, DELETE

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

    // CREATE
    @PostMapping
    public void addEmployee(@RequestBody Employees employee) {
        employeeService.addEmployee(employee);
    }

    // READ MAXIMUM WEEKLY HOURS
    @PostMapping("/update/{id}/hours")
    public String updateMaxHours(@PathVariable int id, @RequestParam double maxWeeklyHours) {
        employeeRepository.updateMaxWeeklyHours(id, maxWeeklyHours);
        return "redirect:/employees"; ///Mangler i frontend, hvis ikke andet?
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
