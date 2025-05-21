package com.example.betasolutions;

import com.example.betasolutions.config.H2TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(H2TestConfig.class)
@Sql(scripts = {
        "classpath:h2init.sql",
        "classpath:final_InsertEmployeeDatabase.sql",
        "classpath:final_InsertProjectDatabase.sql",
        "classpath:final_InsertProjectDatabase.sql",
        "classpath:final_InsertProjectDatabase.sql" // ensure tasks and assignments loaded
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WorkloadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWorkload_withValidSession_shouldReturnWorkloadViewAndModel() throws Exception {
        mockMvc.perform(get("/tasks/workload").sessionAttr("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/workload"))
                .andExpect(model().attributeExists("workloads"))
                .andExpect(model().attribute("workloads", hasSize(3)))
                .andExpect(content().string(containsString("Emil Hansen")))
                .andExpect(content().string(containsString("Sofie Jensen")))
                .andExpect(content().string(containsString("Mads Larsen")));
    }

    @Test
    void getWorkload_withoutSession_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/tasks/workload"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
