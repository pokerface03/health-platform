package com.vitals.subscriber.service;

import com.vitals.subscriber.dao.VitalDao;
import com.vitals.subscriber.record.Vital;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class VitalsSubscriberService {

    private final VitalDao vitalDao;
    public VitalsSubscriberService(VitalDao vitalDao) {
        this.vitalDao = vitalDao;
    }

    public List<Vital> getAllVitals(String userId, Timestamp from, Timestamp to) {
        return vitalDao.getAllVitals(userId, from, to);
    }

    public List<Vital> getVitals(String userId, String metricName, Timestamp from, Timestamp to) {
        return vitalDao.getVitals(userId, metricName, from, to);
    }

    public List<Vital> getLatestVitals(String userId) {
        return vitalDao.getLatestVitals(userId);
    }
}
