package com.example.betasolutions.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

/**
 * Configuration for Projects db connection.
 * This class sets up the primary data source for project-related operations.
 */
@Profile("!test")
@Configuration
public class ProjectsDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties projectsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "projectsDataSource")
    @Primary
    public DataSource projectsDataSource() {
        return projectsDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(projectsDataSource());
    }
}