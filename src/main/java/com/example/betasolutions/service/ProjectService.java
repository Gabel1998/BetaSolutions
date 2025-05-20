package com.example.betasolutions.service;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ProjectService {

    private final SubProjectService subProjectService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
//    Constructor
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository, SubProjectService subProjectService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.subProjectService = subProjectService;
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
                .filter(task ->
                        (task.getProjectId() != null && task.getProjectId().equals((long) projectId)) ||
                                (task.getSubProjectId() != null &&
                                        subProjectService.getSubProjectById(task.getSubProjectId())
                                                .map(sp -> sp.getProjectId().equals(projectId))
                                                .orElse(false))
                )
                .mapToDouble(Task::getActualHours)
                .sum();
    }


    public double getTotalEstimatedHoursForProject(Integer id) {
        return taskRepository.findAll().stream()
                .filter(task ->
                        (task.getProjectId() != null && task.getProjectId().equals((long) id)) ||
                                (task.getSubProjectId() != null &&
                                        subProjectService.getSubProjectById(task.getSubProjectId())
                                                .map(sp -> sp.getProjectId().equals(id))
                                                .orElse(false))
                )
                .mapToDouble(Task::getEstimatedHours)
                .sum();
    }

    /// Vi nester Map<> i Map<> til at nemt kunne trække og beregne 2x key value pairs (est. og actual) i én metode
    public Map<Integer, Map<String, Double>> calculateProjectHoursByProjectIds(List<Integer> projectIds) {
        Map<Integer, Map<String, Double>> result = new HashMap<>();

        for (Integer projectId : projectIds) {
            double totalEstimatedHours = getTotalEstimatedHoursForProject(projectId);
            double totalActualHours = getTotalActualHoursForProject(projectId);

            Map<String, Double> hours = new HashMap<>();
            hours.put("estimatedHours", totalEstimatedHours);
            hours.put("actualHours", totalActualHours);

            result.put(projectId, hours);
        }

        return result;
    }

    public Optional<Project> getProjectById(Long id) {
        if (id == null) return Optional.empty();
        return projectRepository.findById(id.intValue());
    }

}
