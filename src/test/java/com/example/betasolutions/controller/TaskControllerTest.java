package com.example.betasolutions.controller;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.SubProjectService;
import com.example.betasolutions.service.TaskEmployeeService;
import com.example.betasolutions.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;
    private TaskService taskService;
    private ProjectService projectService;
    private SubProjectService subProjectService;
    private TaskEmployeeService taskEmployeeService;
    private EmployeeRepository employeeRepository;
    private TaskController controller;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        projectService = mock(ProjectService.class);
        subProjectService = mock(SubProjectService.class);
        taskEmployeeService = mock(TaskEmployeeService.class);
        employeeRepository = mock(EmployeeRepository.class);

        controller = new TaskController(
                taskService, projectService,
                subProjectService, taskEmployeeService,
                employeeRepository
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers((viewName, locale) -> {
                    if (viewName.startsWith("redirect:")) {
                        return new RedirectView(viewName.substring("redirect:".length()), true);
                    }
                    return new View() {
                        @Override public String getContentType() { return "text/html"; }
                        @Override public void render(Map<String, ?> model,
                                                     jakarta.servlet.http.HttpServletRequest request,
                                                     jakarta.servlet.http.HttpServletResponse response) {
                            // no-op
                        }
                    };
                })
                .build();
    }

    @Test
    void listTasks_notLoggedIn_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void listTasks_loggedIn_showsList() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", "u1");

        SubProject sp = new SubProject();
        sp.setId(7);
        sp.setProjectId(42);
        when(subProjectService.getSubProjectById(7)).thenReturn(Optional.of(sp));

        Task t = new Task();
        t.setId(99L);
        t.setSubProjectId(7);
        t.setStartDate(LocalDate.of(2025,1,1));
        when(taskService.getTasksBySubProjectId(7)).thenReturn(List.of(t));
        when(taskService.isEmployeeOverbooked(any())).thenReturn(false);

        mockMvc.perform(get("/tasks")
                        .param("subProjectId","7")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"))
                .andExpect(model().attribute("tasks", List.of(t)))
                .andExpect(model().attribute("subProject", sp))
                .andExpect(model().attribute("overLimit", false));
    }

    @Test
    void showCreateForm_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/tasks/create").param("subProjectId","1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showCreateForm_loggedIn_showsForm() throws Exception {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user","u");
        SubProject sp = new SubProject(); sp.setId(2); sp.setProjectId(10);
        when(subProjectService.getSubProjectById(2)).thenReturn(Optional.of(sp));

        mockMvc.perform(get("/tasks/create")
                        .param("subProjectId","2")
                        .session(s))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/create"))
                .andExpect(model().attributeExists("task","subProject"));
    }

    @Test
    void showEditForm_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/tasks/edit/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showEditForm_loggedIn_showsEdit() throws Exception {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user","u");
        Task task = new Task(); task.setId(5L); task.setSubProjectId(3);
        when(taskService.getTaskById(5L)).thenReturn(Optional.of(task));
        SubProject sp = new SubProject(); sp.setId(3); sp.setProjectId(8);
        when(subProjectService.getSubProjectById(3)).thenReturn(Optional.of(sp));

        mockMvc.perform(get("/tasks/edit/5").session(s))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/edit"))
                .andExpect(model().attributeExists("task","subProject"));
    }

    @Test
    void deleteTask_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/tasks/delete/9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void deleteTask_loggedIn_deletesAndRedirects() throws Exception {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user","u");
        Task t = new Task(); t.setId(9L); t.setSubProjectId(12);
        when(taskService.getTaskById(9L)).thenReturn(Optional.of(t));

        mockMvc.perform(get("/tasks/delete/9").session(s))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks?subProjectId=12"));
        assertThat(s.getAttribute("successMessage"))
                .isEqualTo("Task deleted successfully");
        verify(taskService).deleteTask(9L);
    }

    @Test
    void getTaskHours_showsListWithHours() throws Exception {
        Task t = new Task(); t.setId(4L); t.setProjectId(100L);
        when(taskService.getTaskById(4L)).thenReturn(Optional.of(t));
        when(taskService.getTotalHoursForTask(4L)).thenReturn(7.5);
        when(projectService.calculateDailyRate(100L)).thenReturn(8.0);

        mockMvc.perform(get("/tasks/task/4/hours"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"))
                .andExpect(model().attribute("totalHours", 7.5))
                .andExpect(model().attribute("dailyRate", 8.0))
                .andExpect(model().attribute("status", "⚠ Under daily rate: (8.0) ⚠"));
    }

    @Test
    void updateMaxHours_redirectsAndUpdates() throws Exception {
        mockMvc.perform(post("/tasks/update/55/hours")
                        .param("maxWeeklyHours","33.3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks/workload"));
        verify(employeeRepository).updateMaxWeeklyHours(55,33.3);
    }

    @Test
    void showWorkload_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/tasks/workload"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showWorkload_loggedIn_showsWorkload() throws Exception {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user","u");
        Employees e1 = new Employees(); e1.setEmId(1); e1.setEmFirstName("A"); e1.setEmLastName("B");
        when(employeeRepository.getAllEmployees()).thenReturn(List.of(e1));

        Map<Long, Map<LocalDate, Pair<Double,Double>>> full = new HashMap<>();
        var dayMap = Map.of(LocalDate.now(), Pair.of(2.0,50.0));
        full.put(1L, dayMap);
        when(taskEmployeeService.getEmployeeLoadOverTime()).thenReturn(full);
        when(employeeRepository.getEmployeeById(1L)).thenReturn(Optional.of(e1));

        mockMvc.perform(get("/tasks/workload").session(s))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/workload"))
                .andExpect(model().attributeExists(
                        "allEmployees","workload","employeeNames","avgWorkload"));
    }

    @Test
    void createTask_valid_createsAndRedirects() throws Exception {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user","u");
        SubProject sp = new SubProject();
        sp.setId(2);
        sp.setProjectId(99);
        when(subProjectService.getSubProjectById(2)).thenReturn(Optional.of(sp));

        mockMvc.perform(post("/tasks/create")
                        .session(s)
                        .param("subProjectId", "2")
                        .param("projectId",    "99")         // ← bind the projectId
                        .param("name",         "T1")
                        .param("startDate",    "2025-01-01")
                        .param("endDate",      "2025-01-07")
                        .param("description",  "Test task"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks?subProjectId=2"));

        assertThat(s.getAttribute("successMessage"))
                .isEqualTo("Task created successfully");
        verify(taskService).createTask(any(Task.class));
    }



    @Test
    void updateTask_valid_updatesAndRedirects() throws Exception {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user","u");

        // stub the SubProject lookup for subProjectId=3
        SubProject sp = new SubProject();
        sp.setId(3);
        sp.setProjectId(77);
        when(subProjectService.getSubProjectById(3)).thenReturn(Optional.of(sp));

        mockMvc.perform(post("/tasks/update/5")
                        .session(s)
                        .param("subProjectId",  "3")
                        .param("projectId",     "77")           // ← must include this so @Valid passes
                        .param("name",           "T1")
                        .param("startDate",      "2025-01-01")
                        .param("endDate",        "2025-01-07")
                        .param("description",    "Test task"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks?subProjectId=3"));

        assertThat(s.getAttribute("successMessage"))
                .isEqualTo("Task updated successfully");

        ArgumentCaptor<Task> cap = ArgumentCaptor.forClass(Task.class);
        verify(taskService).updateTask(cap.capture());
        assertThat(cap.getValue().getId()).isEqualTo(5L);
    }


}
