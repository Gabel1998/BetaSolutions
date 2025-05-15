package com.example.betasolutions.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@TestConfiguration
@Profile("test")
public class H2TestConfig {

    @Bean(name = "projectsDataSource")
    @Primary
    public DataSource projectsDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
                .username("sa")
                .password("")
                .build();
    }

    @Bean(name = "employeesDataSource")
    public DataSource employeesDataSource() {
        // same datasource for simplicity
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:employeesdb;DB_CLOSE_DELAY=-1")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource projectsDataSource) {
        return new JdbcTemplate(projectsDataSource);
    }

    @Bean(name = "employeesJdbcTemplate")
    public JdbcTemplate employeesJdbcTemplate(DataSource employeesDataSource) {
        return new JdbcTemplate(employeesDataSource);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource projectsDataSource) {
        return new DataSourceTransactionManager(projectsDataSource);
    }
}