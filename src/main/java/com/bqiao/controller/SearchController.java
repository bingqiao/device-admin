package com.bqiao.controller;

import com.bqiao.domain.DeviceDoc;
import com.bqiao.service.DeviceDocService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin
@RestController
@RequestMapping("search")
public class SearchController {
    private final DeviceDocService service;

    public SearchController(
            DeviceDocService service) {
        this.service = service;
    }

    @ApiOperation(value = "Find device docs by IDs against Elasticsearch")
    @GetMapping(path = "")
    public ResponseEntity<List<DeviceDoc>> findDevices(
            @ApiParam(value = "Comma separated list of Device IDs")
            @RequestParam(required = false) Optional<List<String>> ids) {
        List<DeviceDoc> devs = service.findDeviceDocs(ids);
        return new ResponseEntity<>(devs, OK);
    }

    @GetMapping(path = "/desc/{desc}")
    public ResponseEntity<List<DeviceDoc>> findDevicesByDesc(@PathVariable String desc) {
        List<DeviceDoc> devs = service.findDevicesByDesc(desc);
        return new ResponseEntity<>(devs, OK);
    }

    @ApiOperation(value = "Find device docs by query against Elasticsearch")
    @GetMapping(path = "/query")
    public ResponseEntity<List<DeviceDoc>> findDevices(
            @ApiParam(value = "Query params including fields, sort fields and sort order")
            @RequestParam(required = false) Map<String,String> params) {
        List<DeviceDoc> devs = service.findDevices(params);
        return new ResponseEntity<>(devs, OK);
    }

    @ApiOperation(value = "Delete device docs by IDs against Elasticsearch")
    @DeleteMapping(path = "")
    public ResponseEntity<Void> delete(
            @ApiParam(value = "Comma separated list of Device IDs")
            @RequestParam(required = false) Optional<List<String>> ids) {
        service.deleteByIds(ids);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteById(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
