package com.example.betasolutions.service;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service-lag for Task – håndterer logik mellem controller og repository
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    public void deleteTask(Long id) {
        taskRepository.delete(id);
    }
}
