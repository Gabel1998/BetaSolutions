package com.example.betasolutions.service;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // konstruktør
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // CREATE
    public void addEmployee(Employees employee) {
        employeeRepository.addEmployee(employee);
    }

    // READ ALL
    public List<Employees> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    // READ BY ID
    public Employees getEmployeeById(long id) {
        return employeeRepository.getEmployeeById(id);
    }

    // UPDATE
    public void updateEmployee(Employees employee) {
        employeeRepository.updateEmployee(employee);
    }

    // DELETE
    public void deleteEmployee(int id) {
        employeeRepository.deleteEmployee(id);
    }
}
