package com.bqiao.service;

import com.bqiao.domain.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceService {
    List<Device> getDevices(Optional<List<String>> ids);
    Device getDevice(String id);
    void deleteDevice(String id);
    Device updateDevice(String id, Device toUpdate);
    Device createDevice(Device toCreate);
    List<Device> getDevices(List<String> ids);
    void deleteDevices(Optional<List<String>> ids);
}
