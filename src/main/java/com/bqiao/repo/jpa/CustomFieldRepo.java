package com.bqiao.repo.jpa;

import com.bqiao.domain.CustomField;
import com.bqiao.domain.CustomFieldIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldRepo extends JpaRepository<CustomField, CustomFieldIdentity> {
    List<CustomField> findByDeviceId(String deviceId);
    List<CustomField> findByDeviceIdIn(List<String> ids);
    List<CustomField> deleteByDeviceId(String deviceId);
    List<CustomField> deleteByDeviceIdIn(List<String> deviceIds);
}
