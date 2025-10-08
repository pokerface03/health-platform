package com.medicine.calendar.daoImpl;

import com.medicine.calendar.dao.MedicineDao;
import com.medicine.calendar.record.Medicine;
import com.medicine.calendar.rowMapper.MedicineRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

    @Repository
    public class MedicineDaoImpl implements MedicineDao {

        protected final JdbcTemplate jdbcTemplate;

        public MedicineDaoImpl(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public List<Medicine> getAllMedicine(String userId) {
            String sql = "SELECT * FROM medicines WHERE user_id = ? ORDER BY time";
            return jdbcTemplate.query(sql, new MedicineRowMapper(), userId);
        }

        @Override
        public Optional<Medicine> getMedicine(Integer id) {
            String sql = "SELECT * FROM medicines WHERE id = ?";
            return jdbcTemplate.query(sql, new MedicineRowMapper(), id).stream().findFirst();
        }

        @Override
        public void addMedicine(Medicine medicine) {
            String sql = "INSERT INTO medicines (user_id, name, dosage, frequency, time, start_date, end_date, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    medicine.user_id(),
                    medicine.name(),
                    medicine.dosage(),
                    medicine.frequency(),
                    medicine.time(),
                    medicine.start_date(),
                    medicine.end_date(),
                    medicine.notes()
            );
        }

        @Override
        public int updateMedicine(Medicine medicine) {
            String sql = "UPDATE medicines SET name = ?, dosage = ?, frequency = ?, time = ?, " +
                    "start_date = ?, end_date = ?, notes = ? WHERE id = ?";
            return jdbcTemplate.update(sql,
                    medicine.name(),
                    medicine.dosage(),
                    medicine.frequency(),
                    medicine.time(),
                    medicine.start_date(),
                    medicine.end_date(),
                    medicine.notes(),
                    medicine.id()
            );
        }

        @Override
        public int deleteMedicine(Integer id) {
            String sql = "DELETE FROM medicines WHERE id = ?";
            return jdbcTemplate.update(sql, id);
        }

        @Override
        public List<Medicine> findActiveMedicinesForDate(LocalDate date, String userId) {
            String sql = "SELECT * FROM medicines WHERE start_date <= ? AND (end_date IS NULL OR end_date >= ?) AND user_id = ? ORDER BY time";
            return jdbcTemplate.query(sql, new MedicineRowMapper(), date, date, userId);
        }
    }
