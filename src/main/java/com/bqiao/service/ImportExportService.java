package com.bqiao.service;

import com.bqiao.domain.Device;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImportExportService {
    List<Device> importDevices(MultipartFile file);
}
