package com.example.betasolutions;
import com.example.betasolutions.model.Project;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.SubProjectService;
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
        SubProjectService subProjectService = mock(SubProjectService.class);
        TaskEmployeeRepository taskEmployeeRepository = mock(TaskEmployeeRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);

        projectService = new ProjectService(
                projectRepository,
                taskRepository,
                subProjectService,
                taskEmployeeRepository,
                employeeRepository
        );
    }

    @Test
    void testCalculateDailyRate() {
        // Arrange
        Project mockProject = new Project();
        mockProject.setEstimatedHours(100);
        mockProject.setStartDate(LocalDate.of(2025, 5, 5)); // Monday
        mockProject.setEndDate(LocalDate.of(2025, 5, 9));   // Friday

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        // Act
        double result = projectService.calculateDailyRate(1L);

        // Assert
        assertEquals(20.0, result); // 100 hours / 5 days
    }

    @Test
    void testCalculateDailyRateWithZeroWorkdays() {
        Project mockProject = new Project();
        mockProject.setEstimatedHours(50);
        mockProject.setStartDate(LocalDate.of(2025, 5, 10)); // Saturday
        mockProject.setEndDate(LocalDate.of(2025, 5, 11));   // Sunday

        when(projectRepository.findById(2L)).thenReturn(Optional.of(mockProject));

        double result = projectService.calculateDailyRate(2L);

        assertEquals(0.0, result);
    }
}

