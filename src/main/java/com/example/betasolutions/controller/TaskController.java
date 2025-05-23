package com.example.betasolutions.controller;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.SubProjectService;
import com.example.betasolutions.service.TaskService;
import com.example.betasolutions.service.TaskEmployeeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages task operations and workload visualization for employees.
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final SubProjectService subProjectService;
    private final TaskEmployeeService taskEmployeeService;
    private final EmployeeRepository employeeRepository;

    // Constructor injection
    public TaskController(TaskService taskService, ProjectService projectService, SubProjectService subProjectService, TaskEmployeeService taskEmployeeService, EmployeeRepository employeeRepository) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.subProjectService = subProjectService;
        this.taskEmployeeService = taskEmployeeService;
        this.employeeRepository = employeeRepository;
    }

    // Verify user authentication
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // Display tasks for a specific subproject
    @GetMapping
    public String listTasks(@RequestParam(value = "subProjectId", required = false) Integer subProjectId, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        SubProject subProject = subProjectService.getSubProjectById(subProjectId)
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        // Sort tasks by start date
        List<Task> tasks = taskService.getTasksBySubProjectId(subProjectId)
                .stream()
                .sorted(Comparator.comparing(Task::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        model.addAttribute("subProject", subProject);

        boolean overLimit = taskService.isEmployeeOverbooked(tasks);
        model.addAttribute("overLimit", overLimit);

        // Handle success message if present in session
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        return "tasks/list";
    }

    // Show form for creating a new task
    @GetMapping("/create")
    public String showCreateForm(@RequestParam("subProjectId") Integer subProjectId,
                                 Model model,
                                 HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        SubProject subProject = subProjectService.getSubProjectById(subProjectId)
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        Task task = new Task();
        task.setSubProjectId(subProjectId);
        task.setProjectId(Long.valueOf(subProject.getProjectId()));

        model.addAttribute("task", task);
        model.addAttribute("subProject", subProject);

        return "tasks/create";
    }

    // Show form for editing an existing task
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Set default start and end dates if not provided
        if (task.getStartDate() == null) {
            task.setStartDate(LocalDate.now());
        }
        if (task.getEndDate() == null) {
            task.setEndDate(LocalDate.now().plusDays(7));
        }

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        task.setProjectId(Long.valueOf(subProject.getProjectId()));

        model.addAttribute("task", task);
        model.addAttribute("subProject", subProject);

        return "tasks/edit";
    }

    // Delete a task and return to task list
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        //get task before deleting to retrieve subProjectId
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Integer subProjectId = task.getSubProjectId();
        //delete task
        taskService.deleteTask(id);

        // Add success message to session
        session.setAttribute("successMessage", "Task deleted successfully");

        return "redirect:/tasks?subProjectId=" + subProjectId;
    }

    // View hours logged against a task
    @GetMapping("/task/{id}/hours")
    public String getTaskHours(@PathVariable("id") Long taskId, Model model) {
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        double totalHours = taskService.getTotalHoursForTask(taskId);
        double dailyRate = projectService.calculateDailyRate(task.getProjectId());

        model.addAttribute("totalHours", totalHours);
        model.addAttribute("dailyRate", dailyRate);
        model.addAttribute("status", totalHours >= dailyRate ? "OK" : "⚠ Under daily rate: (" + dailyRate + ") ⚠");

        return "tasks/list";
    }

    // Update employee's maximum weekly hours
    @PostMapping("/update/{id}/hours")
    public String updateMaxHours(@PathVariable int id, @RequestParam double maxWeeklyHours) {
        employeeRepository.updateMaxWeeklyHours(id, maxWeeklyHours);
        return "redirect:/tasks/workload";
    }

    // Display employee workload visualization
    @GetMapping("/workload")
    public String showWorkload(@RequestParam(required = false) List<Long> employeeIds,
                               Model model,
                               HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        // Get all employees for selection dropdown
        List<Employees> allEmployees = employeeRepository.getAllEmployees();
        model.addAttribute("allEmployees", allEmployees);

        // Get full workload data from service
        Map<Long, Map<LocalDate, Pair<Double, Double>>> fullWorkload = taskEmployeeService.getEmployeeLoadOverTime();

        // if no employees selected, show all employees
        Map<Long, Map<LocalDate, Pair<Double, Double>>> workload;
        if (employeeIds != null && !employeeIds.isEmpty()) {
            // Filter to only selected employees
            workload = new HashMap<>();
            for (Long id : employeeIds) {
                if (fullWorkload.containsKey(id)) {
                    workload.put(id, fullWorkload.get(id));
                }
            }
            model.addAttribute("selectedEmployeeIds", employeeIds);
        } else {
            workload = fullWorkload;
        }

        // Create a map of employee names for display
        Map<Long, String> employeeNames = new HashMap<>();

        // Calculate average workload percentage for each employee
        Map<Long, Double> avgWorkloadPerEmployee = new HashMap<>();

        for (Long employeeId : workload.keySet()) {
            // Get employee name using Optional handling
            Optional<Employees> employeeOptional = employeeRepository.getEmployeeById(employeeId);

            if (employeeOptional.isPresent()) {
                Employees employee = employeeOptional.get();
                employeeNames.put(employeeId, employee.getEmFirstName() + " " + employee.getEmLastName());

                // Calculate average percentage for this employee
                Map<LocalDate, Pair<Double, Double>> employeeData = workload.get(employeeId);
                if (employeeData != null && !employeeData.isEmpty()) {
                    double sum = 0;
                    for (Pair<Double, Double> dailyData : employeeData.values()) {
                        sum += dailyData.getSecond();
                    }
                    double avg = sum / employeeData.size();
                    avgWorkloadPerEmployee.put(employeeId, avg);
                } else {
                    avgWorkloadPerEmployee.put(employeeId, 0.0);
                }
            }
        }

        model.addAttribute("workload", workload);
        model.addAttribute("employeeNames", employeeNames);
        model.addAttribute("avgWorkload", avgWorkloadPerEmployee);

        return "tasks/workload";
    }

    // Process form submission for new task
    @PostMapping("/create")
    public String createTask(@ModelAttribute @Valid Task task,
                             BindingResult result,
                             Model model,
                             HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            return "tasks/create";
        }

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Invalid subproject – could not be found."));

        task.setProjectId(Long.valueOf(subProject.getProjectId()));
        taskService.createTask(task);

        return "redirect:/tasks?subProjectId=" + task.getSubProjectId();
    }

    // Process form submission for updating task
    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute @Valid Task task,
                             BindingResult result,
                             Model model,
                             HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        task.setId(id);

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        // Make sure projectId is set correctly
        task.setProjectId(Long.valueOf(subProject.getProjectId()));

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            model.addAttribute("subProject", subProject);
            return "tasks/edit";
        }

        taskService.updateTask(task);
        return "redirect:/tasks?subProjectId=" + task.getSubProjectId();
    }
}
