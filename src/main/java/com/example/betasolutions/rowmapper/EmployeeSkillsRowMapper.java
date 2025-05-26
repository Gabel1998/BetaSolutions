package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.EmployeeSkills;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeSkillsRowMapper implements RowMapper<EmployeeSkills> {

    @Override
    public EmployeeSkills mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmployeeSkills emSkills = new EmployeeSkills();
        emSkills.setEmskEmId(rs.getInt("emsk_em_id"));
        emSkills.setEmskSkId(rs.getInt("emsk_sk_id"));
        return emSkills;
    }
}