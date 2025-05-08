package com.example.betasolutions.rowmapper;
import com.example.betasolutions.model.Skills;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SkillsRowMapper implements RowMapper<Skills> {

    @Override
    public Skills mapRow(ResultSet rs, int rowNum) throws SQLException {
        Skills skills = new Skills();
        skills.setSkId(rs.getInt("sk_id"));
        skills.setSkName(rs.getString("sk_name"));
        return skills;
    }
}