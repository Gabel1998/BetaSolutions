package com.example.betasolutions.crud_tests;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Starter hele Spring Boot-applikationen i test-kontekst
@SpringBootTest
// Kører SQL-scriptet som ligger i src/test/resources før hver testmetode
@Sql(scripts = "classpath:h2init.sql")
// Sørger for at konteksten nulstilles efter hver test (sletter alt i H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// Sørger for at alle testmetoder kører i en transaktion
@Transactional
// Og at transaktionen bliver rullet tilbage efter testen (så data forbliver ren til næste test)
@Rollback(true)

public class ProjectRepositoryTest {

        @Autowired
        private ProjectRepository projectRepository;


        @Test
        void testProjectCrud() {
            int initialSize = projectRepository.findAll().size();

            // Create
            Project project = new Project();
            project.setName("New Project");
            project.setDescription("Integration test project");
            project.setStartDate(LocalDate.now());
            project.setEndDate(LocalDate.now().plusMonths(1));
            projectRepository.save(project);

            List<Project> allProjects = projectRepository.findAll();
            assertThat(allProjects).hasSize(initialSize + 1);

            // Read
            Project foundProject = allProjects.stream()
                    .filter(p -> p.getName().equals("New Project"))
                    .findFirst()
                    .orElseThrow();
            assertThat(foundProject).isNotNull();

            // Update
            foundProject.setName("Updated Project");
            projectRepository.update(foundProject);
            Project updatedProject = projectRepository.findById(foundProject.getId()).orElseThrow();
            assertThat(updatedProject.getName()).isEqualTo("Updated Project");

            // Delete
            projectRepository.delete(foundProject.getId());
            Optional<Project> deletedProject = projectRepository.findById(foundProject.getId());
            assertThat(deletedProject).isEmpty();
            assertThat(projectRepository.findAll()).hasSize(initialSize);
        }
}
