package com.vitals.subscriber.dao;

import com.vitals.subscriber.record.Vital;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


public interface VitalDao {

    Optional<Vital> getVital (Integer id);
    void addVital (Vital Vital);
    int updateVital (Vital Vital);
    int deleteVital (Integer id);
    List<Vital> getVitals(String userId, String metricName, Timestamp from, Timestamp to);
    List<Vital> getAllVitals(String userId, Timestamp from, Timestamp to);
    List<String> getMetrics(String userId);
    public List<Vital> getLatestVitals(String userId);

}
