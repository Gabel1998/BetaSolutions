package com.example.betasolutions.repository;


import com.example.betasolutions.config.H2TestConfig;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.model.Project;
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

import java.time.LocalDate;
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
public class TaskRepositoryIntegratrionTest {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private TaskRepository taskRepo;

    private long createDummyProject() {
        Project p = new Project();
        p.setName("Parent Project");
        p.setDescription("for tasks");
        p.setStartDate(LocalDate.of(2025, 1, 1));
        p.setEndDate(LocalDate.of(2025, 12, 31));
        projectRepo.save(p);
        return projectRepo.findAll().get(0).getId();
    }

    @Test
    void saveAndGenerateId() {
        // -- Arrange: need a project first
        long projectId = createDummyProject();

        Task t = new Task();
        t.setName("My Test Task");
        t.setDescription("do something");
        t.setEstimatedHours(8.0);
        t.setProjectId(projectId);

        // -- Act
        taskRepo.save(t);

        // -- Assert
        List<Task> all = taskRepo.findAll();
        assertThat(all)
                .extracting(Task::getName)
                .contains("My Test Task");
    }

    @Test
    void findByIdReturnsInsertedRecord() {
        // -- Arrange
        long projectId = createDummyProject();
        Task t = new Task();
        t.setName("Montering af solpaneler");
        t.setDescription("desc");
        t.setEstimatedHours(5.5);
        t.setProjectId(projectId);
        taskRepo.save(t);

        long id = taskRepo.findAll().get(0).getId();

        // -- Act
        Optional<Task> byId = taskRepo.findById(id);

        // -- Assert
        assertThat(byId).isPresent();
        assertThat(byId.get().getName()).isEqualTo("Montering af solpaneler");
    }

    @Test
    void updateModifiesExistingRecord() {
        // -- Arrange
        long projectId = createDummyProject();
        Task original = new Task();
        original.setName("OldTask");
        original.setDescription("x");
        original.setEstimatedHours(3.0);
        original.setProjectId(projectId);
        taskRepo.save(original);

        long id = taskRepo.findAll().get(0).getId();

        Task updated = new Task();
        updated.setId(id);
        updated.setName("NewTaskName");
        updated.setDescription("y");
        updated.setEstimatedHours(4.5);
        updated.setProjectId(projectId);

        // -- Act
        taskRepo.update(updated);

        // -- Assert
        Optional<Task> result = taskRepo.findById(id);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("NewTaskName");
        assertThat(result.get().getEstimatedHours()).isEqualTo(4.5);
    }

}