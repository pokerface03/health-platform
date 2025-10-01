package com.vitals.subscriber.daoImp;

import com.vitals.subscriber.rowMapper.VitalsRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import com.vitals.subscriber.dao.VitalDao;
import com.vitals.subscriber.record.Vital;
import org.springframework.stereotype.Repository;

import java.util.Optional;

    @Repository
    public class VitalDaoImpl implements VitalDao {
        protected final JdbcTemplate jdbcTemplate;

        public VitalDaoImpl(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public Optional<Vital> getVital(Integer id) {
            String sql = "SELECT * FROM vitals v WHERE v.id = ?";
            return jdbcTemplate.query (sql, new VitalsRowMapper(), id).stream().findFirst();
        }
        @Override
        public void addVital(Vital Vital) {
            String sql = "INSERT INTO vitals( user_id, timestamp, metric_name, value) VALUES(?, ?, ?, ?)";
            jdbcTemplate.update(sql, Vital.user_id(), Vital.timestamp(), Vital.metric_name(), Vital.value());
        }
        @Override
        public int updateVital(Vital Vital) {
            String sql = "UPDATE vitals SET user_id = ?, timestamp = ?, metric_name = ?, value = ? WHERE id = ?";
            return jdbcTemplate.update(sql, Vital.user_id(), Vital.timestamp(), Vital.metric_name(), Vital.value(), Vital.id());
        }
        @Override
        public int deleteVital(Integer id) {
            String sql = "DELETE FROM vitals WHERE id = ?";
            return jdbcTemplate.update(sql, id);
        }
    }

