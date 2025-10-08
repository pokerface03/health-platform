package com.vitals.subscriber.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import com.vitals.subscriber.dao.VitalDao;
import com.vitals.subscriber.record.Vital;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vitals")
public class VitalsController {

    private final VitalDao vitalDao;

    public VitalsController(VitalDao vitalDao) {
        this.vitalDao = vitalDao;
    }


    @GetMapping("/latest")
    @ResponseBody
    public List<Vital> getLatestVitals(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        System.out.println(userId);
        return vitalDao.getLatestVitals(userId);
    }

    @GetMapping("/{metricName}")
    @ResponseBody
    public List<Vital> getVitalsByMetric(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String metricName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String to) {

        Timestamp fromTs = from != null ? Timestamp.from(Instant.parse(from))
                : Timestamp.from(Instant.now().minus(24, ChronoUnit.HOURS));
        Timestamp toTs = to != null ? Timestamp.from(Instant.parse(to))
                : Timestamp.from(Instant.now());

        String userId = jwt.getSubject();

        return vitalDao.getVitals(userId, metricName, fromTs, toTs);
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Vital> getAllVitals(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String to) {

        Timestamp fromTs = from != null ? Timestamp.from(Instant.parse(from))
                : Timestamp.from(Instant.now().minus(24, ChronoUnit.HOURS));
        Timestamp toTs = to != null ? Timestamp.from(Instant.parse(to))
                : Timestamp.from(Instant.now());

        String userId = jwt.getSubject();
        return vitalDao.getAllVitals(userId, fromTs, toTs);
    }

    @GetMapping("/health")
    @ResponseBody
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "vitals-monitor");
    }
}
