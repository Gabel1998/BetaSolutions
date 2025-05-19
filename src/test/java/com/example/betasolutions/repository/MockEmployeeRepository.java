package com.example.betasolutions.repository;

import com.example.betasolutions.model.Employees;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@Profile("test")  // Active only during 'test' profile
public class MockEmployeeRepository extends EmployeeRepository {

    public MockEmployeeRepository() {
        super(null); // Pass null since we don't actually need the JdbcTemplate
    }

    @Override
    public List<Employees> getAllEmployees() {
        return Collections.emptyList();
    }

//    @Override
//    public Employees getEmployeeById(int employeeId) {
//        return new Employees(); // Return a dummy employee object
//    }
}