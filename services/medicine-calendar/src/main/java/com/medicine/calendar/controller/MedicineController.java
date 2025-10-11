package com.medicine.calendar.controller;

import com.medicine.calendar.record.Medicine;
import com.medicine.calendar.service.MedicineCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/calendar")

public class MedicineController {

    //@Autowired
    private final MedicineCalendarService medicineCalendarService;

    public MedicineController(MedicineCalendarService medicineCalendarService) {
        this.medicineCalendarService = medicineCalendarService;
    }

    @GetMapping("")
    public ResponseEntity<CalendarResponse> getCalendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int week) {

        String userId = jwt.getSubject();
        LocalDate today = LocalDate.now();

        // Calculate week start (Monday)
        LocalDate weekStart = today.with(DayOfWeek.MONDAY).plusWeeks(week);

        // Generate 7 days
        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(weekStart.plusDays(i));
        }

        // Week range
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String weekRange = weekStart.format(formatter) + " - " +
                weekStart.plusDays(6).format(formatter);

        // Time slots
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
        }

        // Get all medicines
        List<Medicine> allMedicines = medicineCalendarService.getAllMedicines(userId);

        // Build schedule: Map<LocalDate, Map<String, List<Medicine>>>
        Map<String, Map<String, List<Medicine>>> medicineSchedule = new HashMap<>();

        for (LocalDate day : weekDays) {
            Map<String, List<Medicine>> daySchedule = new HashMap<>();
            for (String timeSlot : timeSlots) {
                daySchedule.put(timeSlot, new ArrayList<>());
            }
            medicineSchedule.put(day.toString(), daySchedule);
        }

        // Populate schedule
        for (Medicine medicine : allMedicines) {
            for (LocalDate day : weekDays) {
                if (!medicine.start_date().isAfter(day) &&
                        (medicine.end_date() == null || !medicine.end_date().isBefore(day))) {
                    String hourSlot = String.format("%02d:00", medicine.time().getHour());
                    medicineSchedule.get(day.toString()).get(hourSlot).add(medicine);
                }
            }
        }

        CalendarResponse response = new CalendarResponse(
                userId,
                weekDays,
                weekRange,
                timeSlots,
                medicineSchedule,
                week,
                today,
                allMedicines
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Medicine> addMedicine(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody MedicineRequest request) {

        String userId = jwt.getSubject();
        Medicine medicine = new Medicine(
                request.id(),
                userId,
                request.name(),
                request.dosage(),
                request.frequency(),
                LocalTime.parse(request.time()),
                LocalDate.parse(request.startDate()),
                (request.endDate() != null && !request.endDate().isEmpty())
                        ? LocalDate.parse(request.endDate()) : null,
                request.notes()
        );

        medicineCalendarService.saveMedicine(medicine);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicine);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMedicine(
            @PathVariable Integer id) {

        medicineCalendarService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    // Response DTO
    public record CalendarResponse(
            String userId,
            List<LocalDate> weekDays,
            String weekRange,
            List<String> timeSlots,
            Map<String, Map<String, List<Medicine>>> medicineSchedule,
            int weekOffset,
            LocalDate today,
            List<Medicine> allMedicines
    ) {}

    // Request DTO
    public record MedicineRequest(
            Integer id,
            String name,
            String dosage,
            String frequency,
            String time,
            String startDate,
            String endDate,
            String notes
    ) {}
}
