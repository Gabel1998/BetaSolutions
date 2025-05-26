package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.model.Project;
import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import com.example.betasolutions.repository.TaskEmployeeRepository;
import com.example.betasolutions.repository.TaskRepository;
import com.example.betasolutions.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpSession;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LogControllerTest {

    private MockMvc mockMvc;
    private TaskEmployeeService taskEmployeeService;
    private EmployeeService employeeService;
    private ProjectService projectService;
    private TaskService taskService;
    private TaskRepository taskRepository;
    private TaskEmployeeRepository taskEmployeeRepository;
    private SubProjectService subProjectService;

    @BeforeEach
    void setUp() {
        taskEmployeeService    = mock(TaskEmployeeService.class);
        employeeService        = mock(EmployeeService.class);
        projectService         = mock(ProjectService.class);
        taskService            = mock(TaskService.class);
        taskRepository         = mock(TaskRepository.class);
        taskEmployeeRepository = mock(TaskEmployeeRepository.class);
        subProjectService      = mock(SubProjectService.class);

        LogController controller = new LogController(
                taskEmployeeService,
                employeeService,
                projectService,
                taskService,
                taskRepository,
                taskEmployeeRepository,
                subProjectService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // helper til at oprette en session med "user"
    private MockHttpSession loggedInSession() {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute("user", "u1");
        return s;
    }

    @Test
    void showLogSelection_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/logs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showLogSelection_loggedIn_showsSelection() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());
        when(projectService.getAllProjects()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/logs").session(loggedInSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("logs/select"))
                .andExpect(model().attributeExists("employees", "projects"));
    }

    @Test
    void showSubprojectsAndTasks_emptySubprojects_redirects() throws Exception {
        MockHttpSession s = loggedInSession();
        when(subProjectService.getAllSubProjectsByProjectId(5)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/logs/select")
                        .param("employeeId", "9")
                        .param("projectId", "5")
                        .session(s))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs?emptySubprojects"));

        assertThat(s.getAttribute("employeeId")).isEqualTo(9L);
        assertThat(s.getAttribute("projectId")).isEqualTo(5);
    }

    @Test
    void showSubprojectsAndTasks_withSubprojects_showsForm() throws Exception {
        MockHttpSession s = loggedInSession();
        SubProject sp = new SubProject();
        sp.setId(7);
        sp.setName("SP");
        when(subProjectService.getAllSubProjectsByProjectId(3)).thenReturn(List.of(sp));

        mockMvc.perform(get("/logs/select")
                        .param("employeeId", "2")
                        .param("projectId", "3")
                        .session(s))
                .andExpect(status().isOk())
                .andExpect(view().name("logs/subproject-selection"))
                .andExpect(model().attribute("employeeId", 2L))
                .andExpect(model().attribute("projectId", 3))
                .andExpect(model().attribute("subprojects", List.of(sp)));
    }

    @Test
    void showTaskLogForm_noEmployeeInSession_redirectsToStart() throws Exception {
        mockMvc.perform(get("/logs/fill")
                        .param("subProjectId", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs"));
    }

    @Test
    void showTaskLogForm_emptyTasks_redirectsWithFlag() throws Exception {
        MockHttpSession s = loggedInSession();
        s.setAttribute("employeeId", 11L);

        when(taskRepository.findBySubProjectId(8)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/logs/fill")
                        .param("subProjectId", "8")
                        .session(s))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs/select?employeeId=11&projectId="
                        + s.getAttribute("projectId") + "&emptyTasks=true"));
    }

    @Test
    void showTaskLogForm_withTasks_prefillsAndShowsList() throws Exception {
        MockHttpSession s = loggedInSession();
        s.setAttribute("employeeId", 20L);

        Task t1 = new Task();
        t1.setId(100L);
        t1.setName("T");
        when(taskRepository.findBySubProjectId(2)).thenReturn(List.of(t1));
        when(taskEmployeeRepository.getLoggedHoursForTaskAndEmployee(100L, 20L)).thenReturn(3.5);

        mockMvc.perform(get("/logs/fill")
                        .param("subProjectId", "2")
                        .session(s))
                .andExpect(status().isOk())
                .andExpect(view().name("logs/list"))
                .andExpect(model().attributeExists("tasks"));

        assertThat(t1.getPrefilledHours()).isEqualTo(3.5);
    }

    @Test
    void showDashboard_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/logs/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showDashboard_noFilter_showsOverview() throws Exception {
        MockHttpSession s = loggedInSession();

        // stub employees dropdown
        Employees e = new Employees();
        e.setEmId(1);
        e.setEmFirstName("A");
        e.setEmLastName("B");
        when(employeeService.getAllEmployees()).thenReturn(List.of(e));

        // stub one task
        Task t = new Task();
        t.setId(5L);
        t.setName("X");
        t.setProjectId(10L);
        t.setSubProjectId(20);
        when(taskService.getAllTasks()).thenReturn(List.of(t));
        when(taskService.getTotalHoursForTask(5L)).thenReturn(7.0);

        // brug no-args + setters i stedet for constructor
        Project proj = new Project();
        proj.setId(10);
        proj.setName("P");
        when(projectService.getProjectById(10L)).thenReturn(Optional.of(proj));

        SubProject sp = new SubProject();
        sp.setId(20);
        sp.setName("SP");
        sp.setProjectId(10);
        when(subProjectService.getSubProjectById(20)).thenReturn(Optional.of(sp));

        mockMvc.perform(get("/logs/dashboard").session(s))
                .andExpect(status().isOk())
                .andExpect(view().name("logs/dashboard"))
                .andExpect(model().attributeExists("employees","overview"))
                .andExpect(model().attribute("overview", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void submitLog_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(post("/logs").param("hours_1","2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void submitLog_logsAllParametersAndRedirects() throws Exception {
        MockHttpSession s = loggedInSession();
        s.setAttribute("employeeId", 7L);

        mockMvc.perform(post("/logs")
                        .session(s)
                        .param("hours_3","4.5")
                        .param("foo","bar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs?success"));

        verify(taskEmployeeService).logHours(3L, 7L, 4.5);
    }

    @Test
    void deleteLog_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(post("/logs/delete").param("logId","9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void deleteLog_withEmployee_redirectsToDashboardWithEmployee() throws Exception {
        MockHttpSession s = loggedInSession();

        mockMvc.perform(post("/logs/delete")
                        .session(s)
                        .param("logId","15")
                        .param("employeeId","99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs/dashboard?employeeId=99"));

        verify(taskEmployeeRepository).delete(15L);
    }

    @Test
    void deleteLog_withoutEmployee_redirectsToDashboard() throws Exception {
        MockHttpSession s = loggedInSession();

        mockMvc.perform(post("/logs/delete")
                        .session(s)
                        .param("logId","22"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs/dashboard"));

        verify(taskEmployeeRepository).delete(22L);
    }
}
