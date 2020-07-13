package com.gsd.kolorbi.repository;

import com.gsd.kolorbi.model.DevicePreference;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicePreferenceRepository extends PagingAndSortingRepository<DevicePreference, String> {

    @Query("{ 'deviceId' : ?0 }")
    DevicePreference findByDeviceId(String deviceId) throws Exception;
}
