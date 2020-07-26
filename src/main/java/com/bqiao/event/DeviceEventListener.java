package com.bqiao.event;

import com.bqiao.domain.Device;
import com.bqiao.service.DeviceDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeviceEventListener {
    private final DeviceDocService deviceDocService;

    public DeviceEventListener(DeviceDocService deviceDocService) {
        this.deviceDocService = deviceDocService;
    }

    @EventListener(condition = "#event.success")
    public void handleSuccessful(final LifeCycleEvent<Device> event) {
        log.debug("Device event handling starts===============================");
        switch (event.getAction()) {
            case CREATED:
                log.debug("Event action: " + event.getAction());
                deviceDocService.createDeviceDocs(event.getMessage());
                break;
            case DELETED:
                log.debug("Event action: " + event.getAction());
                deviceDocService.delete(event.getMessage());
                break;
            case UPDATED:
                log.debug("Event action: " + event.getAction());
                deviceDocService.updateDeviceDocs(event.getMessage());
                break;
            default:
                log.error("Unexpected value: " + event.getAction());
        }
        log.debug("Device event handling ends===============================");
    }
}
