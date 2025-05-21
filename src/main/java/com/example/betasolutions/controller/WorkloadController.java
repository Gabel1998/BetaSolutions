// WorkloadController.java
package com.example.betasolutions.controller;

import com.example.betasolutions.dto.WorkloadPerEmployeeDTO;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import com.example.betasolutions.service.TaskEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/workload")
public class WorkloadController {

    @Autowired
    private TaskEmployeeService taskEmployeeService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String showWorkload(Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        Map<Long, Map<LocalDate, Pair<Double, Double>>> rawLoad = taskEmployeeService.getEmployeeLoadOverTime();
        List<Employees> employees = employeeService.getAllEmployees();

        System.out.println("🔍 Found " + employees.size() + " employees");

        List<WorkloadPerEmployeeDTO> workloadList = new ArrayList<>();

        for (Employees emp : employees) {
            Map<LocalDate, Double> dailyPercent = new TreeMap<>();
            Map<LocalDate, Pair<Double, Double>> empLoad = rawLoad.getOrDefault(emp.getEmId().longValue(), new HashMap<>());

            for (int i = 0; i < 7; i++) {
                LocalDate day = LocalDate.now().plusDays(i);
                Pair<Double, Double> load = empLoad.getOrDefault(day, Pair.of(0.0, 0.0));
                dailyPercent.put(day, load.getSecond());
            }
            String name = emp.getEmFirstName() + " " + emp.getEmLastName();
            workloadList.add(new WorkloadPerEmployeeDTO(name, dailyPercent));
        }

        System.out.println("📊 Generated " + workloadList.size() + " workload DTOs");

        model.addAttribute("workloads", workloadList);
        return "tasks/workload";
    }
}
