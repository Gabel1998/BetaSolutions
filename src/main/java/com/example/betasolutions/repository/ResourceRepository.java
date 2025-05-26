package com.example.betasolutions.repository;

import org.springframework.stereotype.Repository;
import com.example.betasolutions.model.Resource;
import com.example.betasolutions.rowmapper.ResourceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

@Repository
public class ResourceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ResourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // READ ALL
    public List<Resource> findAll() {
        String sql = "SELECT * FROM tb_resources";
        return jdbcTemplate.query(sql, new ResourceRowMapper());
    }

    // SAVE
    public void save(Resource resource) {
        String sql = "INSERT INTO tb_resources (re_name, re_co2_per_hour, re_created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, resource.getRe_name(), resource.getRe_co2_perHour(), resource.getRe_created_at());
    }

    // Calculate CO2 for a specific resource
    public double calculateTotalCo2ForProject(int projectId) {
        String sql = "SELECT SUM(tr.tsre_hours_used * r.re_co2_per_hour) AS total_co2 " +
                "FROM tb_tasks_resources tr " +
                "JOIN tb_resources r ON tr.tsre_re_id = r.re_id " +
                "JOIN tb_tasks t ON tr.tsre_ts_id = t.ts_id " +
                "WHERE t.project_id = ?";
        Double result = jdbcTemplate.queryForObject(sql, Double.class, projectId);
        return result != null ? result : 0.0;
    }
}
