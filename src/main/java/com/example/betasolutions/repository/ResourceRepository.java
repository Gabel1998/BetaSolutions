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

    public List<Resource> findAll() {
        String sql = "SELECT * FROM tb_resources";
        return jdbcTemplate.query(sql, new ResourceRowMapper());
    }

    public Resource findById(int id) {
        String sql = "SELECT * FROM tb_resources WHERE re_id = ?";
        return jdbcTemplate.queryForObject(sql, new ResourceRowMapper(), id);
    }

    public void save(Resource resource) {
        String sql = "INSERT INTO tb_resources (re_name, re_co2_per_hour, re_created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, resource.getRe_name(), resource.getRe_co2_perHour(), resource.getRe_created_at());
    }

    public List<Resource> findResourcesByProjectId(int projectId) {
//        String sql = "SELECT * FROM tb_resources WHERE re_project_id = ?";
            String sql = "SELECT DISTINCT r.* FROM tb_resources r " +
                    "JOIN tb_tasks_resources tr ON r.re_id = tr.tsre_re_id " +
                    "JOIN tb_tasks t ON tr.tsre_ts_id = t.ts_id " +
                    "WHERE t.project_id = ?";
            return jdbcTemplate.query(sql, new ResourceRowMapper(), projectId);
    }
}
