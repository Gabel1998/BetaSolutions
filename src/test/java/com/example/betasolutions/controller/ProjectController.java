package com.example.betasolutions.controller;

import com.example.betasolutions.model.Project;
import com.example.betasolutions.service.ProjectService;
import com.example.betasolutions.service.ResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;
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

class ProjectControllerTest {

    private MockMvc mockMvc;
    private ProjectService projectService;
    private ResourceService resourceService;
    private ProjectController controller;

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        resourceService = mock(ResourceService.class);
        controller = new ProjectController(projectService, resourceService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers((viewName, locale) -> {
                    if (viewName.startsWith("redirect:")) {
                        return new RedirectView(viewName.substring("redirect:".length()), true);
                    }
                    return new View() {
                        @Override public String getContentType() { return "text/html"; }

                        @Override
                        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

                        }

                    };
                })
                .build();
    }

    @Test
    void listProjects_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void listProjects_loggedIn_showsList() throws Exception {
        MockHttpSession sess = new MockHttpSession();
        sess.setAttribute("user", "u");
        // prepare two projects
        Project a = new Project(); a.setId(1); a.setStartDate(LocalDate.of(2025,1,1));
        Project b = new Project(); b.setId(2); b.setStartDate(LocalDate.of(2025,2,1));
        when(projectService.getAllProjects()).thenReturn(List.of(b, a));

        // hours map stub
        Map<Integer, Map<String,Double>> hours = Map.of(1, Map.of("est",10.0));
        when(projectService.calculateProjectHoursByProjectIds(List.of(1,2))).thenReturn(hours);
        // co2 stub
        when(resourceService.calculateTotalCo2ForProject(1)).thenReturn(0.0);
        when(resourceService.calculateTotalCo2ForProject(2)).thenReturn(5.0);
        // adjusted hours
        when(projectService.adjustEstimatedHoursBasedOnEfficiency(1)).thenReturn(7.0);
        when(projectService.adjustEstimatedHoursBasedOnEfficiency(2)).thenReturn(8.0);

        mockMvc.perform(get("/projects").session(sess))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/list"))
                .andExpect(model().attributeExists("pageTitle", "projects", "projectHours", "projectCo2", "adjustedHours"));
    }

    @Test
    void showCreateForm_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/projects/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showCreateForm_loggedIn_showsForm() throws Exception {
        MockHttpSession sess = new MockHttpSession(); sess.setAttribute("user","u");
        mockMvc.perform(get("/projects/create").session(sess))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/create"))
                .andExpect(model().attribute("pageTitle","Create Project"))
                .andExpect(model().attributeExists("project"));
    }

    @Test
    void showEditForm_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/projects/edit/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showEditForm_loggedIn_showsEdit() throws Exception {
        MockHttpSession sess = new MockHttpSession(); sess.setAttribute("user","u");
        Project p = new Project(); p.setId(5);
        when(projectService.getProjectById(5)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/projects/edit/5").session(sess))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/edit"))
                .andExpect(model().attribute("pageTitle","Edit Project"))
                .andExpect(model().attribute("project", p));
    }

    @Test
    void deleteProject_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/projects/delete/9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void deleteProject_loggedIn_deletesAndRedirects() throws Exception {
        MockHttpSession sess = new MockHttpSession(); sess.setAttribute("user","u");

        mockMvc.perform(get("/projects/delete/9").session(sess))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/projects"));

        verify(projectService).deleteProject(9);
        assertThat(sess.getAttribute("successMessage")).isEqualTo("Project deleted successfully");
    }

    @Test
    void createProject_valid_createsAndRedirects() throws Exception {
        MockHttpSession sess = new MockHttpSession(); sess.setAttribute("user","u");

        // minimal required fields; add more if your Project has @NotNulls
        mockMvc.perform(post("/projects/create").session(sess)
                        .param("name","P1")
                        .param("startDate","2025-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/projects"));

        verify(projectService).createProject(any(Project.class));
        assertThat(sess.getAttribute("successMessage")).isEqualTo("Project created successfully");
    }

    @Test
    void updateProject_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(post("/projects/update/7"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void updateProject_loggedIn_updatesAndRedirects() throws Exception {
        MockHttpSession sess = new MockHttpSession(); sess.setAttribute("user","u");

        mockMvc.perform(post("/projects/update/7").session(sess)
                        .param("name","NewName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/projects"));

        ArgumentCaptor<Project> cap = ArgumentCaptor.forClass(Project.class);
        verify(projectService).updateProject(cap.capture());
        assertThat(cap.getValue().getId()).isEqualTo(7);
        assertThat(sess.getAttribute("successMessage")).isEqualTo("The project has been updated");
    }

    @Test
    void showProjectSummary_notLoggedIn_redirects() throws Exception {
        mockMvc.perform(get("/projects/summary"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));
    }

    @Test
    void showProjectSummary_loggedIn_showsSummary() throws Exception {
        MockHttpSession sess = new MockHttpSession(); sess.setAttribute("user","u");
        Project p = new Project(); p.setId(11);
        when(projectService.getAllProjects()).thenReturn(List.of(p));
        when(projectService.getTotalActualHoursForProject(11)).thenReturn(5.0);
        when(projectService.getTotalEstimatedHoursForProject(11)).thenReturn(8.0);
        when(projectService.adjustEstimatedHoursBasedOnEfficiency(11)).thenReturn(7.0);

        mockMvc.perform(get("/projects/summary").session(sess))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/summary"))
                .andExpect(model().attributeExists("projects","projectHoursMap","projectEstimatedMap","adjustedHours"));
    }
}
