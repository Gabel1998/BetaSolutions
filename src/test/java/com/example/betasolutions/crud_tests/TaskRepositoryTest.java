package com.example.betasolutions.crud_tests;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.SubProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
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

public class TaskRepositoryTest {

        @Autowired
        private SubProjectRepository subProjectRepository;

        @Autowired
        private TaskRepository taskRepository;


        @Test
        void testTaskCrud() {
            int subProjectId = subProjectRepository.findAll().get(0).getId();
            int initialSize = taskRepository.findAll().size();

            // Create
            Task task = new Task();
            task.setSubProjectId(subProjectId);
            task.setName("New Task");
            task.setDescription("Integration test task");
            task.setEstimatedHours(50.0);
            task.setActualHours(45.0);
            task.setStartDate(LocalDate.of(2024, 1, 1));
            task.setEndDate(LocalDate.of(2024, 1, 31));
            taskRepository.save(task);

            List<Task> allTasks = taskRepository.findAll();
            assertThat(allTasks).hasSize(initialSize + 1);

            // Read
            Task foundTask = allTasks.stream()
                    .filter(t -> t.getName().equals("New Task"))
                    .findFirst()
                    .orElseThrow();
            assertThat(foundTask).isNotNull();

            // ✅ Assert start og slutdato
            assertThat(foundTask.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(foundTask.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));

            // Update
            foundTask.setName("Updated Task");
            taskRepository.update(foundTask);
            Task updatedTask = taskRepository.findById(foundTask.getId()).orElseThrow();
            assertThat(updatedTask.getName()).isEqualTo("Updated Task");

            // Delete
            taskRepository.delete(foundTask.getId());
            Optional<Task> deletedTask = taskRepository.findById(foundTask.getId());
            assertThat(deletedTask).isEmpty();
            assertThat(taskRepository.findAll()).hasSize(initialSize);
        }
}
