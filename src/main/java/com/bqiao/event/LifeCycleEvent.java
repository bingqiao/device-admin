package com.bqiao.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class LifeCycleEvent<T> {
    private boolean success;
    private List<T> message;
    private LifeCycleEventAction action;
}
