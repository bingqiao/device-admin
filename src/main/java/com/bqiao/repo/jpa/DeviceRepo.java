package com.bqiao.repo.jpa;

import com.bqiao.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepo extends JpaRepository<Device, String> {
    List<Device> deleteByIdIn(List<String> ids);
}
