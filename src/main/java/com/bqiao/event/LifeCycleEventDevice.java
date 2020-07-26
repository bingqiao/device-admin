package com.bqiao.event;

import com.bqiao.domain.Device;

import java.util.List;

public class LifeCycleEventDevice extends LifeCycleEvent<Device> {
    public LifeCycleEventDevice(List<Device> message, LifeCycleEventAction action, boolean success) {
        super(success, message, action);
    }
}
