package com.example.betasolutions.service;

import com.example.betasolutions.model.Task;
import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service-lag for Task – håndterer logik mellem controller og repository
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEmployeeRepository taskEmployeeRepository;

    public TaskService(TaskRepository taskRepository, TaskEmployeeRepository taskEmployeeRepository) {
        this.taskRepository = taskRepository;
        this.taskEmployeeRepository = taskEmployeeRepository;
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

    public double getTotalHoursForTask(Long taskId) {
        List<TaskEmployee> employees = taskEmployeeRepository.findByTaskId(taskId);
        return employees.stream()
                .mapToDouble(TaskEmployee::getHoursWorked)
                .sum();
    }

    public double calculateDailyHours(TaskEmployee employee) {
        long workdays = DateUtils.countWorkdays(employee.getStartDate(), employee.getEndDate());
        if (workdays == 0) return 0;
        return (employee.getAllocatedHours() * employee.getAllocationPercentage()) / workdays;
    }

    public double getTotalDailyHoursForTask(Long taskId) {
        List<TaskEmployee> employees = taskEmployeeRepository.findByTaskId(taskId);
        return employees.stream()
                .mapToDouble(this::calculateDailyHours)
                .sum();
    }

    public boolean isDailyHoursExceeded(List<Task> tasks, double dailyLimit) {
        double totalHours = 0;
        for (Task task : tasks) {
            if (task.getActualHours() != null) {
                totalHours += task.getActualHours();
            }
        }
        return totalHours > dailyLimit;
    }

    public List<Task> getTasksBySubProjectId(int subProjectId) {
        return taskRepository.findBySubProjectId(subProjectId);
    }
}
