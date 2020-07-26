package com.bqiao.service.impl;

import com.bqiao.domain.CustomField;
import com.bqiao.domain.Device;
import com.bqiao.event.LifeCycleEventPublisher;
import com.bqiao.repo.RepositoryService;
import com.bqiao.service.DeviceService;
import com.bqiao.service.ImportExportService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.bqiao.event.LifeCycleEventAction.CREATED;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class ImportExportServiceImpl implements ImportExportService {

    private final RepositoryService repoService;
    private final DeviceService deviceService;
    private final LifeCycleEventPublisher publisher;

    public ImportExportServiceImpl(
            RepositoryService repoService,
            DeviceService deviceService,
            LifeCycleEventPublisher publisher) {
        this.repoService = repoService;
        this.deviceService = deviceService;
        this.publisher = publisher;
    }

    @Override
    public List<Device> importDevices(MultipartFile file) {
        List<String[]> data = getCsvLines(file);
        List<Device> devices = data.stream().map(this::parseDevice).collect(toList());
        List<Device> devicesCreated = repoService.getDeviceRepo().saveAll(devices);
        List<CustomField> fieldsToCreate = IntStream
                .range(0, devicesCreated.size())
                .mapToObj(i -> {
                    Device deviceCreated = devicesCreated.get(i);
                    Map<Integer, String> fields = devices.get(i).getCustomFields();
                    return fields.keySet().stream().map(k -> CustomField.builder().deviceId(deviceCreated.getId()).fieldOrder(k).value(fields.get(k)).build());
                }).flatMap(s -> s).collect(toList());
        repoService.getCustomFieldRepo().saveAll(fieldsToCreate);
        List<Device> devicesWithFields = deviceService.getDevices(devicesCreated.stream().map(Device::getId).collect(toList()));
        publisher.publish(devicesWithFields, CREATED, true);
        return devicesWithFields;
    }

    private List<String[]> getCsvLines(MultipartFile file) {
        List<String[]> data = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();) {
            data = csvReader.readAll().stream().filter(line -> line.length > 1).collect(toList());
        } catch (IOException | CsvException e) {
            log.error("Failed to parse uploaded file", e);
        }
        return data;
    }

    //"UID","Profile","Hostname","Description","IP Address","Ext IP Addr","Last User","Agent Version","Model",
    // "Operating System","Serial Number","Motherboard","Custom field 1","Custom field 2","Custom field 3",
    // "Custom field 4","Custom field 5"
    private Device parseDevice(String[] fields) {
        Device device = Device.parse(fields);
        Map<Integer, String> customFields = CustomField.parse(fields);
        device.setCustomFields(customFields);
        return device;
    }
}
