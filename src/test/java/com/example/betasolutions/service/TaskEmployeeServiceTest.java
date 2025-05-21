// TaskEmployeeServiceTest.java
package com.example.betasolutions.service;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.TaskEmployee;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskEmployeeServiceTest {

    @Mock
    private TaskEmployeeRepository taskEmployeeRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private TaskEmployeeService service;

    private Employees emp;

    @BeforeEach
    void setUp() {
        emp = new Employees();
        emp.setEmId(1);
        emp.setMaxWeeklyHours(40.0);
        when(employeeRepository.getEmployeeById(1L)).thenReturn(emp);
    }

    @Test
    void getEmployeeLoadOverTime_fordeler_timer_jævnt_og_beregner_100pct() {
        TaskEmployee te = new TaskEmployee();
        te.setEmployeeId(1L);
        te.setHoursWorked(40.0);
        te.setStartDate(LocalDate.of(2025,5,1));
        te.setEndDate(LocalDate.of(2025,5,5));

        when(taskEmployeeRepository.findAll()).thenReturn(List.of(te));

        Map<Long, Map<LocalDate, Pair<Double, Double>>> result = service.getEmployeeLoadOverTime();

        assertThat(result).containsKey(1L);
        Map<LocalDate, Pair<Double, Double>> daily = result.get(1L);

        for (LocalDate d = LocalDate.of(2025,5,1);
             !d.isAfter(LocalDate.of(2025,5,5)); d = d.plusDays(1)) {
            Pair<Double, Double> p = daily.get(d);
            assertThat(p.getFirst()).isEqualTo(8.0);
            assertThat(p.getSecond()).isEqualTo(100.0);
        }
        assertThat(daily).hasSize(5);
        verify(taskEmployeeRepository).findAll();
        verify(employeeRepository).getEmployeeById(1L);
    }
}