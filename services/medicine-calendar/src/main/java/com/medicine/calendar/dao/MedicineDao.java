package com.medicine.calendar.dao;

import com.medicine.calendar.record.Medicine;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicineDao {

    List<Medicine> getAllMedicine(String userId);

    Optional<Medicine> getMedicine(Integer id);

    void addMedicine(Medicine medicine);

    int updateMedicine(Medicine medicine);

    int deleteMedicine(Integer id);

    List<Medicine> findActiveMedicinesForDate(LocalDate date, String userId);
}
