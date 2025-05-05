package com.example.betasolutions.service;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.SubProjectRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Service-lag for SubProject – håndterer logik mellem controller og repository
@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;
    private final TaskRepository taskRepository;

    public SubProjectService(SubProjectRepository subProjectRepository, TaskRepository taskRepository) {
        this.subProjectRepository = subProjectRepository;
        this.taskRepository = taskRepository;
    }

    public void createSubProject(SubProject subProject) {
        subProjectRepository.save(subProject);
    }

    public List<SubProject> getAllSubProjects() {
        return subProjectRepository.findAll();
    }

    public Optional<SubProject> getSubProjectById(Integer id) {
        return subProjectRepository.findById(id);
    }

    public void updateSubProject(SubProject subProject) {
        subProjectRepository.update(subProject);
    }

    public void deleteSubProject(Integer id) {
        subProjectRepository.delete(id);
    }

    public Optional<SubProject> findSubProjectById(int subProjectId) {
        return subProjectRepository.findById(subProjectId);
    }

    public Map<String, Object> calculateSubProjectOverview(int subProjectId) {
        List<Task> tasks = taskRepository.findBySubProjectId(subProjectId);

        double totalEstimated = tasks.stream()
                .mapToDouble(t -> t.getEstimatedHours() != null ? t.getEstimatedHours() : 0)
                .sum();

        double totalActual = tasks.stream()
                .mapToDouble(t -> t.getActualHours() != null ? t.getActualHours() : 0)
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
}
