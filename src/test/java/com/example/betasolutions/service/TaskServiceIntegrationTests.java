package com.example.betasolutions.service;

// TaskServiceIntegrationTests.java
import com.example.betasolutions.config.H2TestConfig;
import com.example.betasolutions.model.Task;
import org.junit.jupiter.api.DisplayName;
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
public class TaskServiceIntegrationTests {

    @Autowired
    private TaskService taskService;

    @Test
    @DisplayName("Get all tasks")
    void testGetAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        assertThat(tasks).hasSize(3);
    }

    @Test
    @DisplayName("Get task by ID")
    void testGetTaskById() {
        Optional<Task> maybe = taskService.getTaskById(100001L);
        assertThat(maybe).isPresent();
        assertThat(maybe.get().getName()).isEqualTo("Trækning af kabler");
    }

    @Test
    @DisplayName("Get tasks by subproject ID")
    void testGetTasksBySubProject() {
        List<Task> tasks = taskService.getTasksBySubProjectId(2000);
        assertThat(tasks).anyMatch(t -> "Montering af solpaneler".equals(t.getName()));
    }

    @Test
    @DisplayName("Calculate total hours for a task")
    void testGetTotalHoursForTask() {
        double hours = taskService.getTotalHoursForTask(100000L);
        assertThat(hours).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Get assigned employee names for a task")
    void testGetAssignedEmployeeNames() {
        List<String> names = taskService.getAssignedEmployeeNames(100000);
        assertThat(names).isNotEmpty();
    }

    @Test
    @DisplayName("Check overbooking")
    void testIsEmployeeOverbooked() {
        boolean overbooked = taskService.isEmployeeOverbooked(taskService.getAllTasks());
        assertThat(overbooked).isFalse();
    }
}