package com.example.betasolutions.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class EmployeesDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.employees")
    public DataSourceProperties employeesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource employeesDataSource() {
        return employeesDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate employeesJdbcTemplate() {
        return new JdbcTemplate(employeesDataSource());
    }
}