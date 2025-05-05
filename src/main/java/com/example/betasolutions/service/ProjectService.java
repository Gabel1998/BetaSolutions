package com.example.betasolutions.service;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
//    Constructor
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public void createProject(Project project) {
        projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(int id) {
        return projectRepository.findById(id);
    }

    public void deleteProject(int id) {
        projectRepository.delete(id);
    }

    public void updateProject(Project project) {
        projectRepository.update(project);
    }

    public double calculateDagRate (Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) return 0;

        Project project = projectOpt.get();
        double totalHours = project.getEstimatedHours();

        long workdays = countWorkdays(project.getStartDate(), project.getEndDate());

        return (workdays == 0) ? 0 : totalHours / workdays;
    }

    public long countWorkdays(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .filter(d -> !d.getDayOfWeek().equals(DayOfWeek.SATURDAY) &&
                        !d.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count();
    }

    public Object findSubProjectById(int subProjectId) {
        return projectRepository.findById(subProjectId)
                .orElseThrow(() -> new IllegalArgumentException("SubProject not found with id: " + subProjectId));
    }

    public double getTotalActualHoursForProject(int projectId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getProjectId() != null && task.getProjectId().equals((long) projectId))
                .mapToDouble(task -> task.getActualHours() != null ? task.getActualHours() : 0)
                .sum();
    }


}
