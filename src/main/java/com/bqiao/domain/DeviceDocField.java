package com.bqiao.domain;

import java.util.Arrays;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public enum DeviceDocField {
    PROFILE("profile", true),
    HOST("host", false),
    DESC("desc", false),
    IP("ip", false),
    EXTIP("extIp", false),
    LASTUSER("lastUser", false),
    AGENTVER("agentVer", true),
    MODEL("model", false),
    OS("os", false),
    SN("sn", false),
    MB("mb", false),
    CUSTOMFIELDS("customFields", false);

    private final String text;
    private final boolean sortable;

    DeviceDocField(String text, boolean sortable) {
        this.text = text;
        this.sortable = sortable;
    }
    public String getText() {
        return this.text;
    }
    public boolean isSortable() {
        return this.sortable;
    }
    public static DeviceDocField parseText(String text) {
        if (isEmpty(text)) {
            return null;
        }
        if (text.matches("^c_\\d$")) {
            return CUSTOMFIELDS;
        }
        return Arrays.stream(DeviceDocField.values())
                .filter(v -> v.text.equalsIgnoreCase(text))
                .findAny()
                .orElse(null);
    }
}
