package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.service.EmployeeService;
import com.example.betasolutions.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

class HomeControllerTest {

    private MockMvc mockMvc;
    private ProjectService projectService;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        employeeService = mock(EmployeeService.class);
        HomeController controller = new HomeController(projectService, employeeService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers((viewName, locale) -> new View() {
                    @Override public String getContentType() { return "text/html"; }
                    @Override public void render(Map<String, ?> model,
                                                 jakarta.servlet.http.HttpServletRequest request,
                                                 jakarta.servlet.http.HttpServletResponse response) {}
                })
                .build();
    }

    @Test
    void updateUserProfile_returnsOk() throws Exception {
        mockMvc.perform(post("/users/update")
                        .param("employeeId", "1")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("emEfficiency", "0.9")
                        .param("maxWeeklyHours", "40"))
                .andExpect(status().isOk());
        // No service interaction expected currently
    }

    @Test
    void cookies_returnsCookiesView() throws Exception {
        mockMvc.perform(get("/cookies"))
                .andExpect(status().isOk())
                .andExpect(view().name("cookies"));
    }

    @Test
    void privacy_returnsPrivacyView() throws Exception {
        mockMvc.perform(get("/privacy"))
                .andExpect(status().isOk())
                .andExpect(view().name("privacy"));
    }

    @Test
    void showUserSettings_notLoggedIn_returnsOk() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/users/settings").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void showUserSettings_userNotFound_returnsOk() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", "john");
        when(employeeService.findByUsername("john")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/settings").session(session))
                .andExpect(status().isOk());

        // service findByUsername still invoked
    }

    @Test
    void showUserSettings_userFound_modelPopulated() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", "john");
        Employees emp = new Employees();
        emp.setEmId(1);
        when(employeeService.findByUsername("john")).thenReturn(Optional.of(emp));
        when(employeeService.getAllEmployees()).thenReturn(Collections.singletonList(emp));

        mockMvc.perform(get("/users/settings").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("users/settings"))
                .andExpect(model().attributeExists("allEmployees", "employee", "pageTitle"));
    }
}
