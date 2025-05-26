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

        when(projectRepository.findById(1Lgi)).thenReturn(Optional.of(mockProject));

        // Act
        double result = projectService.calculateDailyRate(1L);

        // Assert
        assertEquals(20.0, result); // 100 hours / 5 days
        verify(projectRepository).findById(1L);
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
        verify(projectRepository).findById(2L);
    }

    @Test
    void testCalculateDailyRateWithProjectNotFound() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        double result = projectService.calculateDailyRate(999L);

        // Assert
        assertEquals(0.0, result);
        verify(projectRepository).findById(999L);
    }

    @Test
    void testCountWorkdays() {
        // Test with a full work week (Monday to Friday)
        LocalDate start = LocalDate.of(2025, 5, 5); // Monday
        LocalDate end = LocalDate.of(2025, 5, 9);   // Friday
        long workdays = projectService.countWorkdays(start, end);
        assertEquals(5, workdays);

        // Test with a period that includes weekend days
        start = LocalDate.of(2025, 5, 5);  // Monday
        end = LocalDate.of(2025, 5, 12);   // Next Monday (includes weekend)
        workdays = projectService.countWorkdays(start, end);
        assertEquals(6, workdays); // 5 days from first week + Monday from second week

        // Test with just one day
        workdays = projectService.countWorkdays(start, start);
        assertEquals(1, workdays);
    }
}
