package com.example.betasolutions.controller;

import com.example.betasolutions.model.Employees;
import com.example.betasolutions.repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginControllerTest {

    private MockMvc mockMvc;
    private EmployeeRepository employeeRepository;
    private LoginController controller;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        controller = new LoginController();
        ReflectionTestUtils.setField(controller, "employeeRepository", employeeRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers((viewName, locale) -> {
                    if (viewName.startsWith("redirect:")) {
                        // strip off "redirect:" and let RedirectView set the 3xx
                        return new org.springframework.web.servlet.view.RedirectView(
                                viewName.substring("redirect:".length()), true);
                    }
                    // otherwise a no‐op HTML view stub
                    return new org.springframework.web.servlet.View() {
                        @Override public String getContentType() { return "text/html"; }
                        @Override public void render(Map<String, ?> model,
                                                     jakarta.servlet.http.HttpServletRequest req,
                                                     jakarta.servlet.http.HttpServletResponse resp) {
                            // no-op
                        }
                    };
                })
                .build();
    }

    @Test
    void showLoginForm_returnsLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("pageTitle", "Login"));
    }

    @Test
    void logout_invalidatesSessionAndRedirects() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", "someone");

        mockMvc.perform(get("/auth/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void showRegisterForm_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("pageTitle", "Register"));
    }

    @Test
    void registerWithFirstName_createsUserAndRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("username", "u1")
                        .param("password", "p1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("emEfficiency", "1.2")
                        .param("maxWeeklyHours", "35.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        ArgumentCaptor<Employees> captor = ArgumentCaptor.forClass(Employees.class);
        verify(employeeRepository).registerNewUser(captor.capture());
        Employees saved = captor.getValue();
        assertThat(saved.getEmUsername()).isEqualTo("u1");
        assertThat(saved.getEmFirstName()).isEqualTo("John");
    }

    @Test
    void registerWithoutFirstName_setsSessionAndRedirectsToProjects() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/auth/register")
                        .param("username", "u2")
                        .param("password", "p2")
                        // no firstName
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/projects"));

        assertThat(session.getAttribute("user")).isEqualTo("u2");
    }

    @Test
    void loginValid_setsSessionUserAndRedirectsToProjects() throws Exception {
        Employees e = new Employees();
        e.setEmUsername("abc");
        e.setEmPassword("pass");
        when(employeeRepository.findByUsername("abc")).thenReturn(Optional.of(e));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/auth/login")
                        .param("username", "abc")
                        .param("password", "pass")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/projects"));

        assertThat(session.getAttribute("user")).isEqualTo("abc");
    }

    @Test
    void loginInvalid_setsErrorAndRedirectsToLogin() throws Exception {
        when(employeeRepository.findByUsername("bad")).thenReturn(Optional.empty());
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/auth/login")
                        .param("username", "bad")
                        .param("password", "wrong")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        assertThat(session.getAttribute("errorMessage")).isEqualTo("Invalid credentials");
    }
}
