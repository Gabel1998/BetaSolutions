package com.example.betasolutions.repository;

import com.example.betasolutions.config.H2TestConfig;
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
public class ProjectRepositoryIntegrationTest {

    @Autowired
    private ProjectRepository repo;

    @Test
    void saveAndGenerateId() {
        // -- Arrange
        var project = new Project();
        project.setName("Test Project");
        project.setDescription("Just a test");
        project.setStartDate(LocalDate.of(2025, 1, 1));
        project.setEndDate(LocalDate.of(2025, 12, 31));

        // -- Act
        repo.save(project);

        // -- Assert
        List<Project> all = repo.findAll();
        assertThat(all)
                .extracting(Project::getName)
                .contains("Test Project");
    }

    @Test
    void findByIdReturnsInsertedRecord() {
        // -- Arrange: insert a known project via setters
        var p = new Project();
        p.setName("Grøn Energi");
        p.setDescription("desc");
        p.setStartDate(LocalDate.of(2025,5,1));
        p.setEndDate(LocalDate.of(2025,5,31));
        repo.save(p);

        long id = repo.findAll().get(0).getId();

        // -- Act
        Optional<Project> byId = repo.findById(id);

        // -- Assert
        assertThat(byId).isPresent();
        assertThat(byId.get().getName()).isEqualTo("Grøn Energi");
    }

    @Test
    void updateModifiesExistingRecord() {
        // -- Arrange: save an “Old” project
        var old = new Project();
        old.setName("OldName");
        old.setDescription("x");
        old.setStartDate(LocalDate.now());
        old.setEndDate(LocalDate.now());
        repo.save(old);

        long id = repo.findAll().get(0).getId();

        // prepare the updated version
        var updated = new Project();
        updated.setId((int) id);
        updated.setName("NewName");
        updated.setDescription("y");
        updated.setStartDate(LocalDate.now());
        updated.setEndDate(LocalDate.now());

        // -- Act
        repo.update(updated);

        // -- Assert
        Optional<Project> result = repo.findById(id);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("NewName");
    }

    @Test
    void deleteRemovesById() {
        // -- Arrange: insert a project to delete
        var toDelete = new Project();
        toDelete.setName("ToDelete");
        toDelete.setDescription("");
        toDelete.setStartDate(LocalDate.now());
        toDelete.setEndDate(LocalDate.now());
        repo.save(toDelete);

        long id = repo.findAll().get(0).getId();

        // -- Act
        repo.delete((int) id);

        // -- Assert
        assertThat(repo.findById(id)).isEmpty();
    }
}
