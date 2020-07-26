package com.bqiao.service;

import com.bqiao.domain.Device;
import com.bqiao.domain.DeviceDoc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DeviceDocService {
    String SEARCH_PARAM_SORT = "s";
    String SEARCH_PARAM_ORDER = "o";
    String SEARCH_PARAM_MUST = "m";

    DeviceDoc createDeviceDoc(Device device);
    List<DeviceDoc> createDeviceDocs(List<Device> devices);
    List<DeviceDoc> updateDeviceDocs(List<Device> devices);
    void deleteById(String id);
    void deleteByIds(Optional<List<String>> ids);
    void delete(List<Device> devices);

    List<DeviceDoc> findDevicesByDesc(String desc);

    List<DeviceDoc> findDeviceDocs(Optional<List<String>> ids);

    List<DeviceDoc> findDevices(Map<String, String> params);
}
