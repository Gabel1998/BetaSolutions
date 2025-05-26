package com.example.betasolutions.service;

// EmployeeServiceIntegrationTests.java
import com.example.betasolutions.config.H2TestConfig;
import com.example.betasolutions.model.Employees;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

// Starts the entire Spring Boot application in test context
@SpringBootTest
@ActiveProfiles("test")
@Import(H2TestConfig.class)
// Disable referential integrity to allow dropping tables without foreign key errors
@Sql(statements = "SET REFERENTIAL_INTEGRITY FALSE")
// Runs the SQL script located in src/test/resources before each test method & continue on errors (e.g., drops of non-existing tables)
@Sql(scripts = "classpath:h2init.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
// Re-enables referential integrity after the script runs
@Sql(statements = "SET REFERENTIAL_INTEGRITY TRUE")
// Ensures the context is reset after each test (deletes everything in H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// Ensures all test methods run in a transaction
@Transactional
// And that the transaction is rolled back after the test (so data remains clean for the next test)
@Rollback(true)
public class EmployeeServiceIntegrationTests {

    @Autowired
    private EmployeeService employeeService;

    @Test
//  Get all employees
    void testGetAllEmployees() {
        List<Employees> employees = employeeService.getAllEmployees();
        assertThat(employees).hasSize(3);
    }

    @Test
//  Get employee by ID
    void testGetEmployeeById() {
        Optional<Employees> employee = employeeService.getEmployeeById(7000L);
        assertThat(employee).isPresent();
        assertThat(employee.get().getEmLastName()).isEqualTo("Hansen");
    }

    @Test
//  Find employee by username
    void testFindByUsername() {
        Optional<Employees> maybe = employeeService.findByUsername("sofie.jensen");
        assertThat(maybe).isPresent();
        assertThat(maybe.get().getEmFirstName()).isEqualTo("Sofie");
    }

    @Test
//  Create, update and delete employee
    void testCrudEmployee() {
        // Create
        Employees emp = new Employees();
        emp.setEmFirstName("Temp");
        emp.setEmLastName("User");
        emp.setEmUsername("temp.user");
        emp.setEmPassword("pwd");
        emp.setEmEfficiency(1.0);
        emp.setMaxWeeklyHours(40.0);
        employeeService.addEmployee(emp);

        Optional<Employees> created = employeeService.findByUsername("temp.user");
        assertThat(created).isPresent();

        // Update
        Employees updated = created.get();
        updated.setEmLastName("Updated");
        employeeService.updateEmployee(updated);
        Optional<Employees> afterUpdate = employeeService.getEmployeeById(updated.getEmId());
        assertThat(afterUpdate.get().getEmLastName()).isEqualTo("Updated");

        // Delete
        employeeService.deleteEmployee(updated.getEmId().intValue());
        Optional<Employees> deleted = employeeService.getEmployeeById(updated.getEmId());
        assertThat(deleted).isEmpty();
    }
}
