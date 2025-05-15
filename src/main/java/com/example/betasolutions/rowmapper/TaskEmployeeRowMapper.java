package com.example.betasolutions.rowmapper;

import com.example.betasolutions.model.TaskEmployee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaskEmployeeRowMapper implements RowMapper<TaskEmployee> {
    @Override
    public TaskEmployee mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskEmployee te = new TaskEmployee();
        te.setTseId( rs.getLong("tse_id") );
        te.setTaskId( rs.getLong("tse_ts_id") );
        te.setEmployeeId( rs.getLong("tse_em_id") );
        te.setHoursWorked( rs.getDouble("tse_hours_worked") );


        Date sd = rs.getDate("start_date");
        if(sd != null) te.setStartDate( sd.toLocalDate() );
        Date ed = rs.getDate("end_date");
        if(ed != null) te.setEndDate( ed.toLocalDate() );
        return te;
    }

}