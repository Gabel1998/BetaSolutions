package com.example.betasolutions.service;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
//    Constructor
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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


}
