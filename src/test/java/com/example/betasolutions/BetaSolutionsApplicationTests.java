package com.example.betasolutions;

import com.example.betasolutions.config.H2TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(H2TestConfig.class)
class BetaSolutionsApplicationTests {

    @Test
    void contextLoads() {
    }

}
