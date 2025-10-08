package com.medicine.calendar.record;

import java.time.LocalDate;
import java.time.LocalTime;

public record Medicine(Integer id, String user_id, String name, String dosage, String frequency, LocalTime time, LocalDate start_date, LocalDate end_date, String notes) {
}
