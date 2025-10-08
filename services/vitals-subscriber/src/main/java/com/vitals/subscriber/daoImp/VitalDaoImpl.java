package com.vitals.subscriber.daoImp;

import com.vitals.subscriber.rowMapper.VitalsRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import com.vitals.subscriber.dao.VitalDao;
import com.vitals.subscriber.record.Vital;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
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

        @Override
        public List<Vital> getVitals(String userId, String metricName, Timestamp from, Timestamp to) {
            String sql = "SELECT * FROM vitals WHERE user_id = ? AND metric_name = ? " +
                    "AND timestamp BETWEEN ? AND ? ORDER BY timestamp";
            return jdbcTemplate.query(sql, new VitalsRowMapper(), userId, metricName, from, to);
        }

        @Override
        public List<Vital> getAllVitals(String userId, Timestamp from, Timestamp to) {
            String sql = "SELECT * FROM vitals WHERE user_id = ? " +
                    "AND timestamp BETWEEN ? AND ? ORDER BY timestamp";
            return jdbcTemplate.query(sql, new VitalsRowMapper(), userId, from, to);
        }

        @Override
        public List<Vital> getLatestVitals(String userId) {
            String sql = "SELECT DISTINCT ON (metric_name) id, user_id, timestamp, metric_name, value " +
                    "FROM vitals WHERE user_id = ? ORDER BY metric_name, timestamp DESC";
            return jdbcTemplate.query(sql, new VitalsRowMapper(), userId);
        }

        @Override
        public List<String> getMetrics(String userId) {
            String sql = "SELECT DISTINCT metric_name FROM vitals WHERE user_id = ? ORDER BY metric_name";
            return jdbcTemplate.queryForList(sql, String.class, userId);
        }
    }

