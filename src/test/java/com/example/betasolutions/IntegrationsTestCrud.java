package com.example.betasolutions;

import com.example.betasolutions.config.H2TestConfig;
import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.SubProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Starts the entire Spring Boot application in test context
@SpringBootTest
@ActiveProfiles("test")
@Import(H2TestConfig.class)
// Runs the SQL script located in src/test/resources before each test method
@Sql(scripts = "classpath:h2init.sql")
// Ensures the context is reset after each test (deletes everything in H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// Ensures all test methods run in a transaction
@Transactional
// And that the transaction is rolled back after the test (so data remains clean for the next test)
@Rollback(true)
class IntegrationsTestCrud {

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
        assertThat(foundProject.getId()).isPositive(); // Verify ID was generated
        assertThat(foundProject.getDescription()).isEqualTo("Integration test project"); // Verify description
        assertThat(foundProject.getStartDate()).isEqualTo(LocalDate.now()); // Verify start date
        assertThat(foundProject.getEndDate()).isEqualTo(LocalDate.now().plusMonths(1)); // Verify end date

        // Update
        foundProject.setName("Updated Project");
        foundProject.setDescription("Updated description");
        projectRepository.update(foundProject);
        Project updatedProject = projectRepository.findById(foundProject.getId()).orElseThrow();
        assertThat(updatedProject.getName()).isEqualTo("Updated Project");
        assertThat(updatedProject.getDescription()).isEqualTo("Updated description"); // Verify multiple fields

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
        assertThat(foundSubProject.getId()).isPositive(); // Verify ID was generated
        assertThat(foundSubProject.getProjectId()).isEqualTo(projectId); // Verify relationship
        assertThat(foundSubProject.getDescription()).isEqualTo("Integration test subproject");
        assertThat(foundSubProject.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(foundSubProject.getEndDate()).isEqualTo(LocalDate.now().plusMonths(1));

        // Update
        foundSubProject.setName("Updated SubProject");
        foundSubProject.setDescription("Updated subproject description");
        subProjectRepository.update(foundSubProject);
        SubProject updatedSubProject = subProjectRepository.findById(foundSubProject.getId()).orElseThrow();
        assertThat(updatedSubProject.getName()).isEqualTo("Updated SubProject");
        assertThat(updatedSubProject.getDescription()).isEqualTo("Updated subproject description");

        // Verify relationship integrity is maintained
        assertThat(updatedSubProject.getProjectId()).isEqualTo(projectId);

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
        assertThat(foundTask.getId()).isPositive();
        assertThat(foundTask.getSubProjectId()).isEqualTo(subProjectId);
        assertThat(foundTask.getDescription()).isEqualTo("Integration test task");

        // Verify task-specific properties
        assertThat(foundTask.getEstimatedHours()).isEqualTo(50.0);
        assertThat(foundTask.getActualHours()).isEqualTo(45.0);

        // Assert start and end date
        assertThat(foundTask.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(foundTask.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));

        // Update
        foundTask.setName("Updated Task");
        foundTask.setEstimatedHours(60.0);
        foundTask.setDescription("Updated task description");
        taskRepository.update(foundTask);
        Task updatedTask = taskRepository.findById(foundTask.getId()).orElseThrow();
        assertThat(updatedTask.getName()).isEqualTo("Updated Task");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated task description");
        assertThat(updatedTask.getEstimatedHours()).isEqualTo(60.0); // Verify updated hours

        // Verify relationship integrity is maintained
        assertThat(updatedTask.getSubProjectId()).isEqualTo(subProjectId);

        // Delete
        taskRepository.delete(foundTask.getId());
        Optional<Task> deletedTask = taskRepository.findById(foundTask.getId());
        assertThat(deletedTask).isEmpty();
        assertThat(taskRepository.findAll()).hasSize(initialSize);
    }
}
