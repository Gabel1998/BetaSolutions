// WorkloadControllerTest.java
package com.example.betasolutions.Controller;

import com.example.betasolutions.controller.WorkloadController;
import com.example.betasolutions.dto.WorkloadPerEmployeeDTO;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import com.example.betasolutions.service.TaskEmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadControllerTest {

    @Mock
    private TaskEmployeeService taskEmployeeService;
    @Mock
    private EmployeeService employeeService;
    @InjectMocks
    private WorkloadController controller;

    @Test
    void showWorkload_redirect_til_login_hvis_ikke_logget_ind() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpSession session = new MockHttpSession();
        String view = controller.showWorkload(model, session);
        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    void showWorkload_lægger_workloads_i_model_og_returnerer_tasks_workload() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "bruger");

        Employees emp = new Employees();
        emp.setEmId(1);
        emp.setEmFirstName("Test");
        emp.setEmLastName("User");
        when(employeeService.getAllEmployees()).thenReturn(List.of(emp));

        Map<LocalDate, Pair<Double, Double>> load = new TreeMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            load.put(today.plusDays(i), Pair.of(0.0, (double)(i * 10)));
        }
        when(taskEmployeeService.getEmployeeLoadOverTime())
                .thenReturn(Map.of(1L, load));

        ExtendedModelMap model = new ExtendedModelMap();
        String view = controller.showWorkload(model, session);

        assertThat(view).isEqualTo("tasks/workload");

        @SuppressWarnings("unchecked")
        List<WorkloadPerEmployeeDTO> list = (List<WorkloadPerEmployeeDTO>) model.get("workloads");
        assertThat(list).hasSize(1);

        WorkloadPerEmployeeDTO dto = list.get(0);
        assertThat(dto.employeeName()).isEqualTo("Test User");
        assertThat(dto.getDailyLoadPercent().get(today)).isEqualTo(0.0);
        assertThat(dto.getDailyLoadPercent().get(today.plusDays(6))).isEqualTo(60.0);

        verify(employeeService).getAllEmployees();
        verify(taskEmployeeService).getEmployeeLoadOverTime();
    }
}