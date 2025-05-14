package com.example.betasolutions.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.betasolutions.repository",
        entityManagerFactoryRef = "employeesEntityManagerFactory",
        transactionManagerRef = "employeesTransactionManager"
)
public class EmployeesJpaConfig {
    // No beans needed here - they're in JpaConfig
}