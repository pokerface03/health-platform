package com.vitals.subscriber.dao;

import com.vitals.subscriber.record.Vital;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface VitalDao {

    Optional<Vital> getVital (Integer id);
    void addVital (Vital Vital);
    int updateVital (Vital Vital);
    int deleteVital (Integer id);

}
