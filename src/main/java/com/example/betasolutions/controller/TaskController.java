package com.example.betasolutions.controller;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.SubProjectService;
import com.example.betasolutions.service.TaskService;
import com.example.betasolutions.service.TaskEmployeeService;
import com.example.betasolutions.service.EmployeeService;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final SubProjectService subProjectService;
    private final TaskEmployeeService taskEmployeeService;
    private final EmployeeRepository employeeRepository;

    public TaskController(TaskService taskService, ProjectService projectService, SubProjectService subProjectService, TaskEmployeeService taskEmployeeService, EmployeeRepository employeeRepository) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.subProjectService = subProjectService;
        this.taskEmployeeService = taskEmployeeService;

        this.employeeRepository = employeeRepository;
    }

    // Check if user is logged in
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    // GET endpoints for basic CRUD operations
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

        return "tasks/list";
    }


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

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        SubProject subProject = subProjectService.getSubProjectById(task.getSubProjectId())
                .orElseThrow(() -> new RuntimeException("Subproject not found"));

        task.setProjectId(Long.valueOf(subProject.getProjectId()));

        model.addAttribute("task", task);
        model.addAttribute("subProject", subProject);

        return "tasks/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/auth/login";
        //get task before deleting to retrieve subProjectId
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Integer subProjectId = task.getSubProjectId();
        //delete task
        taskService.deleteTask(id);
        return "redirect:/tasks?subProjectId=" + subProjectId;
    }

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

    @GetMapping("/workload")
    public String showWorkload(@RequestParam(required = false) List<Long> employeeIds,
                              @RequestParam(defaultValue = "week") String period,
                              Model model,
                              HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        // Get all employees for selection dropdown
        List<Employees> allEmployees = employeeRepository.getAllEmployees();
        model.addAttribute("allEmployees", allEmployees);
        model.addAttribute("selectedPeriod", period);

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
            // Get employee name
            Employees employee = employeeRepository.getEmployeeById(employeeId);
            if (employee != null) {
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
            System.out.println(">>> VALIDATION ERRORS");
            model.addAttribute("task", task);
            model.addAttribute("subProject", subProject);
            return "tasks/edit";
        }

        taskService.updateTask(task);
        return "redirect:/tasks?subProjectId=" + task.getSubProjectId();
    }
}
