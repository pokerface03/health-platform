package com.vitals.subscriber.controller;

import com.vitals.subscriber.service.MqttSubscriberService;
import org.apache.catalina.connector.Response;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private final VitalDao vitalDao;
    private final MqttSubscriberService mqttSubscriberService;

    public VitalsController(VitalDao vitalDao) throws MqttException {
        this.vitalDao = vitalDao;
        this.mqttSubscriberService = new MqttSubscriberService(vitalDao);
        mqttSubscriberService.connectAndSubscribe();
    }

    @GetMapping("")
    ResponseEntity<Response> getUserId(@ AuthenticationPrincipal Jwt jwt) throws MqttException {
        String userId = jwt.getSubject();
        mqttSubscriberService.setUserId(userId);
        return null;
    }

    @GetMapping("/latest")
    @ResponseBody
    public List<Vital> getLatestVitals(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        System.out.println("userId="+userId);
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
