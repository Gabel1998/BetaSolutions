package com.example.betasolutions.crud_tests;


import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.SubProjectRepository;
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

public class SubProjectRepositoryTest {

        @Autowired
        private SubProjectRepository subProjectRepository;
        private ProjectRepository projectRepository;

        @Test
        void testSubProjectCrud() {
            int projectId = projectRepository.findAll().get(0).getId();
            int initialSize = subProjectRepository.findAll().size();

            // Create
            SubProject subProject = new SubProject();
            subProject.setProjectId(projectId);
            subProject.setName("New SubProject");
            subProject.setDescription("Integration test subproject");
            subProject.setStartDate(LocalDate.now());
            subProject.setEndDate(LocalDate.now().plusMonths(1));
            subProjectRepository.save(subProject);

            List<SubProject> allSubProjects = subProjectRepository.findAll();
            assertThat(allSubProjects).hasSize(initialSize + 1);

            // Read
            SubProject foundSubProject = allSubProjects.stream()
                    .filter(sp -> sp.getName().equals("New SubProject"))
                    .findFirst()
                    .orElseThrow();
            assertThat(foundSubProject).isNotNull();

            // Update
            foundSubProject.setName("Updated SubProject");
            subProjectRepository.update(foundSubProject);
            SubProject updatedSubProject = subProjectRepository.findById(foundSubProject.getId()).orElseThrow();
            assertThat(updatedSubProject.getName()).isEqualTo("Updated SubProject");

            // Delete
            subProjectRepository.delete(foundSubProject.getId());
            Optional<SubProject> deletedSubProject = subProjectRepository.findById(foundSubProject.getId());
            assertThat(deletedSubProject).isEmpty();
            assertThat(subProjectRepository.findAll()).hasSize(initialSize);
        }

}
