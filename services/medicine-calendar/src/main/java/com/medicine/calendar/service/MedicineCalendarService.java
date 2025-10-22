package com.medicine.calendar.service;

import com.medicine.calendar.dao.MedicineDao;
import com.medicine.calendar.record.Medicine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineCalendarService {

    private final MedicineDao medicineDao;

    public MedicineCalendarService(MedicineDao medicineDao) {
        this.medicineDao = medicineDao;
    }

    public List<Medicine> getAllMedicines(String user_id) {
        return medicineDao.getAllMedicine(user_id);
    }

    public Optional<Medicine> getMedicine(Integer id) {
        return medicineDao.getMedicine(id);
    }

    public void saveMedicine(Medicine medicine) {
        if (medicine.id() == null) {
            medicineDao.addMedicine(medicine);
        } else {
            medicineDao.updateMedicine(medicine);
        }
    }

    public void deleteMedicine(Integer id) {
        medicineDao.deleteMedicine(id);
    }

    public List<Medicine> getActiveMedicinesForDate(LocalDate date, String user_id) {
        return medicineDao.findActiveMedicinesForDate(date, user_id);
    }
}
