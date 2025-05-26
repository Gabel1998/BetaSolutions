package com.example.betasolutions.service;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.SubProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Service layer for SubProject – handles logic between the controller and the repository
@Service
@Transactional(readOnly = true)
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    // Constructor
    public SubProjectService(SubProjectRepository subProjectRepository, TaskRepository taskRepository, TaskService taskService) {
        this.subProjectRepository = subProjectRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    // CREATE
    @Transactional
    public void createSubProject(SubProject subProject) {
        subProjectRepository.save(subProject);
    }

    // READ BY ID
    public Optional<SubProject> getSubProjectById(Integer id) {
        return subProjectRepository.findById(id);
    }

    // Calculate overview for a specific sub-project
    public Map<String, Object> calculateSubProjectOverview(int subProjectId) {
        List<Task> tasks = taskRepository.findBySubProjectId(subProjectId);

        double totalEstimated = tasks.stream()
                .mapToDouble(Task::getEstimatedHours)
                .sum();

        double totalActual = tasks.stream()
                .mapToDouble(Task::getActualHours)
                .sum();

        SubProject subProject = subProjectRepository.findById(subProjectId)
                .orElseThrow(() -> new RuntimeException("SubProject not found"));

        long workdays = DateUtils.countWorkdays(subProject.getStartDate(), subProject.getEndDate());
        double dagrate = workdays > 0 ? totalEstimated / workdays : 0;
        String status = totalActual >= dagrate ? "OK" : "⚠ Under dagrate ⚠";

        Map<String, Object> result = new HashMap<>();
        result.put("subProject", subProject);
        result.put("tasks", tasks);
        result.put("totalEstimated", totalEstimated);
        result.put("totalActual", totalActual);
        result.put("dagrate", dagrate);
        result.put("status", status);

        return result;
    }

    // READ ALL sub-projects by project ID
    public List<SubProject> getAllSubProjectsByProjectId(Integer projectId) {
        return subProjectRepository.findAllByProjectId(projectId);
    }

    // Calculate total estimated and actual hours for tasks in multiple sub-projects
    public Map<Integer, Map<String, Double>> calculateTaskHoursBySubProjectIds(List<Integer> subProjectIds) {
        Map<Integer, Map<String, Double>> result = new HashMap<>();

        for(Integer subProjectId : subProjectIds) {
            double totalEstimatedHours = 0;
            double totalActualHours = 0;

            List<Task> tasks = taskService.getTasksBySubProjectId(subProjectId);
            for (Task task : tasks) {
                totalEstimatedHours += task.getEstimatedHours();
                totalActualHours += task.getActualHours();
            }

            Map<String, Double> hours = new HashMap<>();
            hours.put("estimatedHours", totalEstimatedHours);
            hours.put("actualHours", totalActualHours);

            result.put(subProjectId, hours);
        }
        return result;
    }

    // UPDATE
    @Transactional
    public void updateSubProject(SubProject subProject) {
        subProjectRepository.update(subProject);
    }

    // DELETE
    @Transactional
    public void deleteSubProject(Integer id) {
        subProjectRepository.delete(id);
    }
}
