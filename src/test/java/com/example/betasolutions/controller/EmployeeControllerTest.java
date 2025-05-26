package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private MockMvc mockMvc;
    private EmployeeService employeeService;
    private final String BASE = "/employees";

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class);
        EmployeeController controller = new EmployeeController();
        ReflectionTestUtils.setField(controller, "employeeService", employeeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllEmployees_returnsOk() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk());

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeById_found_returnsOk() throws Exception {
        Employees emp = new Employees();
        emp.setEmId(1);
        when(employeeService.getEmployeeById(1)).thenReturn(Optional.of(emp));

        mockMvc.perform(get(BASE + "/{emId}", 1))
                .andExpect(status().isOk());

        verify(employeeService).getEmployeeById(1);
    }

    @Test
    void getEmployeeById_notFound_returns404() throws Exception {
        when(employeeService.getEmployeeById(42)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE + "/{emId}", 42))
                .andExpect(status().isNotFound());

        verify(employeeService).getEmployeeById(42);
    }

    @Test
    void addEmployee_returnsOk() throws Exception {
        String json = "{}";

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(employeeService).addEmployee(any(Employees.class));
    }

    @Test
    void updateEmployee_returnsOk() throws Exception {
        String json = "{}";

        mockMvc.perform(put(BASE + "/{emId}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        // because the controller sets emId via the @PathVariable
        verify(employeeService).updateEmployee(argThat(e -> e.getEmId() == 5));
    }

    @Test
    void deleteEmployee_returnsOk() throws Exception {
        mockMvc.perform(delete(BASE + "/{emId}", 7))
                .andExpect(status().isOk());

        verify(employeeService).deleteEmployee(7);
    }
}
