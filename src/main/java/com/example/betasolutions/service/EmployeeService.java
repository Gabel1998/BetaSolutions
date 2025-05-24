package com.example.betasolutions.service;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // CONSTRUCTOR
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
    public Optional<Employees> getEmployeeById(long id) {
        return employeeRepository.getEmployeeById(id);
    }

    // FIND BY USERNAME
    public Optional<Employees> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
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
