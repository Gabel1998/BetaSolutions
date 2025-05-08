package com.example.betasolutions;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.SubProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import org.junit.jupiter.api.*;
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
class CrudTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SubProjectRepository subProjectRepository;

    @Autowired
    private TaskRepository taskRepository;

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
