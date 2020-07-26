package com.bqiao.event;

import com.bqiao.domain.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LifeCycleEventPublisher {
    private final ApplicationEventPublisher publisher;
    public LifeCycleEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(final List<Device> message, LifeCycleEventAction action, boolean success) {
        log.debug("Publishing lifeCycle event.");
        final LifeCycleEvent<Device> event = new LifeCycleEventDevice(message, action, success);
        publisher.publishEvent(event);
    }

    public void publishIds(final List<String> ids, LifeCycleEventAction action, boolean success) {
        publish(ids.stream().map(id -> Device.builder().id(id).build()).collect(Collectors.toList()), action, success);
    }
}
