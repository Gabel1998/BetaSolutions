package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.SubProject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//Mapper en database-række til et SubProjekt-objekt
public class SubProjectRowMapper implements RowMapper<SubProject> {

    @Override
    public SubProject mapRow(ResultSet rs, int rowNum) throws SQLException {
        SubProject sp = new SubProject();
        sp.setId(rs.getInt("sp_id"));
        sp.setProjectId(rs.getInt("sp_p_id"));
        sp.setName(rs.getString("sp_name"));
        sp.setDescription(rs.getString("sp_description"));
        sp.setStartDate(rs.getDate("start_date").toLocalDate());
        sp.setEndDate(rs.getDate("end_date").toLocalDate());
        sp.setCreatedAt(rs.getTimestamp("sp_created_at").toLocalDateTime());
        sp.setUpdatedAt(rs.getTimestamp("sp_updated_at").toLocalDateTime());

        return sp;
    }

}
