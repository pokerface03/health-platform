package com.medicine.calendar.rowMapper;

import com.medicine.calendar.record.Medicine;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class MedicineRowMapper implements RowMapper<Medicine> {
    @Override
    public Medicine mapRow(ResultSet rs, int rowNum) throws SQLException {

        return  new Medicine(
                rs.getInt("id"),
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("dosage"),
                rs.getString("frequency"),
                rs.getTime("time").toLocalTime(),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                rs.getString("notes")
        );
    }
}
