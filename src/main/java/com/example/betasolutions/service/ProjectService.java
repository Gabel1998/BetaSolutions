package com.example.betasolutions.service;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.repository.ProjectRepository;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectService {

    private final SubProjectService subProjectService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskEmployeeRepository taskEmployeeRepository;
    private final EmployeeRepository employeeRepository;
//    Constructor
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository, SubProjectService subProjectService, TaskEmployeeRepository taskEmployeeRepository, EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.subProjectService = subProjectService;
        this.taskEmployeeRepository = taskEmployeeRepository;
        this.employeeRepository = employeeRepository;
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

    public double calculateDailyRate(Long projectId) {
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

    public double adjustEstimatedHoursBasedOnEfficiency(int projectId) {
        double totalEstimatedHours = getTotalEstimatedHoursForProject(projectId);

        List<TaskEmployee> taskEmployees = taskEmployeeRepository.findByProjectId(projectId);

        if (taskEmployees.isEmpty()) {
            return totalEstimatedHours; // No employees assigned, return original estimate
        }

        double averageEfficiency = taskEmployees.stream()
                .map(te -> employeeRepository.getEmployeeById(te.getEmployeeId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToDouble(Employees::getEmEfficiency)
                .average()
                .orElse(1.0); // Default to 1.0 if no valid efficiencies found

        return averageEfficiency > 0 ? totalEstimatedHours / averageEfficiency : totalEstimatedHours;
    }

}
