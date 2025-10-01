package com.vitals.subscriber.rowMapper;

import com.vitals.subscriber.record.Vital;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VitalsRowMapper implements RowMapper<Vital> {
    @Override
    public Vital mapRow (ResultSet rs, int rowNum) throws SQLException {
        return new Vital(
                rs.getInt("id"),
                rs.getString ("user_id"),
                rs.getTimestamp("timestamp"),
                rs.getString ("metric_name"),
                rs.getDouble("value")
        );
    }
}
