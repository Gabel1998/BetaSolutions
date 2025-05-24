package com.example.betasolutions.service;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Service layer for Task – handles logic between the controller and the repository
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEmployeeRepository taskEmployeeRepository;
    private final EmployeeService employeeService;

    // Constructor
    public TaskService(TaskRepository taskRepository, TaskEmployeeRepository taskEmployeeRepository, EmployeeService employeeService) {
        this.taskRepository = taskRepository;
        this.taskEmployeeRepository = taskEmployeeRepository;
        this.employeeService = employeeService;
    }
    // timestamp for task creation and update
    public void createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    // READ ALL
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // READ BY ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Calculate total hours worked for a specific task
    public double getTotalHoursForTask(Long taskId) {
        List<TaskEmployee> employees = taskEmployeeRepository.findByTaskId(taskId);
        return employees.stream()
                .mapToDouble(TaskEmployee::getHoursWorked)
                .sum();
    }

    // Calculate total hours worked for all tasks in a sub-project
    public List<Task> getTasksBySubProjectId(int subProjectId) {
        return taskRepository.findBySubProjectId(subProjectId);
    }

    // Get all tasks assigned to a specific employee
    public List<String> getAssignedEmployeeNames (Integer taskId) {
        return taskEmployeeRepository.findAssignedEmployeeNamesByTaskId(taskId);
    }

    // Check if any employee is overbooked based on their allocated hours across tasks
    public boolean isEmployeeOverbooked(List<Task> tasks) {
        Map<Long, Double> employeeTotalHours = new HashMap<>();

        // Loop through each task and its assigned employees
        for (Task task : tasks) {
            List<TaskEmployee> taskEmployees = taskEmployeeRepository.findByTaskId(task.getId());
            // Loop through each employee assigned to task
            for (TaskEmployee te : taskEmployees) {
                Long employeeId = te.getEmployeeId();
                // Add hours to employee's total
                double currentTotal = employeeTotalHours.getOrDefault(employeeId, 0.0);
                employeeTotalHours.put(employeeId, currentTotal + te.getAllocatedHours());
            }
        }
        // Validate if any employee exceeds their max allowed hours
        for (Map.Entry<Long, Double> entry : employeeTotalHours.entrySet()) {
            long employeeId = entry.getKey();
            double totalHours = entry.getValue();

            // Fetch employee from DB to get max allowed weekly hours
            Optional<Employees> employeeOptional = employeeService.getEmployeeById(employeeId);
            double maxAllowedHours = employeeOptional.map(Employees::getMaxWeeklyHours).orElse(40.0);

            if (totalHours > maxAllowedHours) {
                return true;
            }
        }
        return false;
    }

    // UPDATE
    public void updateTask(Task task) {
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.update(task);
    }

    // DELETE
    public void deleteTask(Long id) {
        taskRepository.delete(id);
    }
}
