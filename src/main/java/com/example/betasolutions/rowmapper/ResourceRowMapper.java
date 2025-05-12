package com.example.betasolutions.rowmapper;
import com.example.betasolutions.model.Resource;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceRowMapper implements RowMapper<Resource> {

    @Override
    public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Resource(
                rs.getInt("re_id"),
                rs.getString("re_name"),
                rs.getDouble("re_co2_per_hour"),
                rs.getTimestamp("re_created_at").toLocalDateTime()
        );
    }
}
