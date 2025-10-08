package com.medicine.calendar.controller;

import com.medicine.calendar.record.Medicine;
import com.medicine.calendar.service.MedicineCalendarService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//@Validated
@Controller
@RequestMapping("/calendar")
public class MedicineController {

    @Autowired
    private final MedicineCalendarService medicineCalendarService;

    public MedicineController(MedicineCalendarService medicineCalendarService) {
        this.medicineCalendarService = medicineCalendarService;
    }

    @GetMapping("/{userId}")
    public String calendar(@PathVariable String userId, @RequestParam(defaultValue = "0") int week, Model model) {
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
        Map<LocalDate, Map<String, List<Medicine>>> medicineSchedule = new HashMap<>();

        for (LocalDate day : weekDays) {
            Map<String, List<Medicine>> daySchedule = new HashMap<>();
            for (String timeSlot : timeSlots) {
                daySchedule.put(timeSlot, new ArrayList<>());
            }
            medicineSchedule.put(day, daySchedule);
        }

        // Populate schedule
        for (Medicine medicine : allMedicines) {
            for (LocalDate day : weekDays) {
                if (! medicine.start_date().isAfter(day) &&
                        (medicine.end_date() == null ||! medicine.end_date().isBefore(day))) {
                    String hourSlot = String.format("%02d:00", medicine.time().getHour());
                    medicineSchedule.get(day).get(hourSlot).add(medicine);
                }
            }
        }

        model.addAttribute("userId", userId);
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("weekRange", weekRange);
        model.addAttribute("timeSlots", timeSlots);
        model.addAttribute("medicineSchedule", medicineSchedule);
        model.addAttribute("weekOffset", week);
        model.addAttribute("today", today);
        model.addAttribute("allMedicines", allMedicines);
        model.addAttribute("medicine", new Medicine(null, "userId", "", "", "", null, LocalDate.now(), null, ""));

        return "calendar";
    }

    @PostMapping("/{user_id}/add")
    public String addMedicine(@PathVariable (value="user_id", required=true) String user_id,
                              @RequestParam(required = false) Integer id,
                              @RequestParam String name,
                              @RequestParam String dosage,
                              @RequestParam String frequency,
                              @RequestParam String time,
                              @RequestParam String startDate,
                              @RequestParam(required = false) String endDate,
                              @RequestParam(required = false) String notes,
                              @RequestParam(defaultValue = "0") int week) {



        medicineCalendarService.saveMedicine(new Medicine(id, user_id, name, dosage, frequency, LocalTime.parse(time), LocalDate.parse(startDate), (endDate!= null && !endDate.isEmpty())? LocalDate.parse(endDate):null, notes));
        return "redirect:/calendar/" + user_id + "?week=" + week;
    }

    @PostMapping("/{user_id}/delete/{id}")
    public String deleteMedicine(@PathVariable String user_id,
                                 @PathVariable String id,
                                 @RequestParam(defaultValue = "0") int week) {
        String[] parts = id.split("\\(");

        medicineCalendarService.deleteMedicine(Integer.parseInt(parts[0]));
        return "redirect:/calendar/" + user_id + "?week=" + week;
    }
}
