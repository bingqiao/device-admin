package com.bqiao.controller;

import com.bqiao.domain.Device;
import com.bqiao.service.ImportExportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("importexport")
@Slf4j
public class ImportExportController {
    private final ImportExportService service;

    public ImportExportController(ImportExportService service) {
        this.service = service;
    }

    @ApiOperation(value = "Upload csv of devices to be imported")
    @PostMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public List<Device> upload(
            @ApiParam(value = "File to upload")
            @RequestPart MultipartFile file) {
        return service.importDevices(file);
    }
}
