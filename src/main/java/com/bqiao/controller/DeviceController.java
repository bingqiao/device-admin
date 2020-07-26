package com.bqiao.controller;

import com.bqiao.domain.Device;
import com.bqiao.service.DeviceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

/**
 * Operations for Managing Devices
 */
@CrossOrigin
@RestController
@RequestMapping("device")
public class DeviceController {
    private final DeviceService service;

    public DeviceController(
            DeviceService service) {
        this.service = service;
    }

    @ApiOperation(value = "Get devices by IDs")
    @GetMapping(path = "")
    public ResponseEntity<List<Device>> getDevices(
            @ApiParam(value = "Comma separated list of Device IDs")
            @RequestParam(required = false) Optional<List<String>> ids) {
        List<Device> devs = service.getDevices(ids);
        return new ResponseEntity<>(devs, OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable String id) {
        Device device = service.getDevice(id);
        return new ResponseEntity<>(device, OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        service.deleteDevice(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @ApiOperation(value = "Delete devices by IDs")
    @DeleteMapping(path = "")
    public ResponseEntity<Void> deleteDevices(
            @ApiParam(value = "Comma separated list of Device IDs")
            @RequestParam Optional<List<String>> ids) {
        service.deleteDevices(ids);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable String id, @RequestBody Device toUpdate) {
        Device device = service.updateDevice(id, toUpdate);
        return new ResponseEntity<>(device, OK);
    }

    @PostMapping(path = "")
    public ResponseEntity<Device> createDevice(@RequestBody Device toCreate) {
        Device device = service.createDevice(toCreate);
        return new ResponseEntity<>(device, CREATED);
    }
}
