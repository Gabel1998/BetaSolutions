package com.example.betasolutions;
import com.example.betasolutions.model.Project;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        taskRepository = mock(TaskRepository.class);
//        projectService = new ProjectService(projectRepository, taskRepository, null);
    }

    @Test
    void testCalculateDagRate() {
        // Arrange
        Project mockProject = new Project();
        mockProject.setEstimatedHours(100);
        mockProject.setStartDate(LocalDate.of(2025, 5, 5)); // fx mandag
        mockProject.setEndDate(LocalDate.of(2025, 5, 9));   // fredag

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        // Act
        double result = projectService.calculateDagRate(1L);

        // Assert
        assertEquals(20.0, result); // 100 timer / 5 dage
    }

    @Test
    void testCalculateDagRateWithZeroWorkdays() {
        Project mockProject = new Project();
        mockProject.setEstimatedHours(50);
        mockProject.setStartDate(LocalDate.of(2025, 5, 10)); // lørdag
        mockProject.setEndDate(LocalDate.of(2025, 5, 11));   // søndag

        when(projectRepository.findById(2L)).thenReturn(Optional.of(mockProject));

        double result = projectService.calculateDagRate(2L);

        assertEquals(0.0, result);
    }
}


