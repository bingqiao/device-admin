package com.bqiao.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Device {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String profile;

    private String hostname;
    private String description;
    private String ipAddress;
    private String extIpAddress;
    private String lastUser;
    private int agentVersion;
    private String model;
    private String os;
    private String serialNumber;
    private String motherboard;

    @Transient
    private Map<Integer, String> customFields;

    @CreatedDate
    private long created;

    @LastModifiedDate
    private long modified;

    public Device copy(Device toCopy) {
        if (toCopy == null) {
            return this;
        }
        this.setProfile(toCopy.getProfile());
        this.setAgentVersion(toCopy.getAgentVersion());
        this.setDescription(toCopy.getDescription());
        this.setExtIpAddress(toCopy.getExtIpAddress());
        this.setHostname(toCopy.getHostname());
        this.setIpAddress(toCopy.getIpAddress());
        this.setModel(toCopy.getModel());
        this.setMotherboard(toCopy.getMotherboard());
        this.setOs(toCopy.getOs());
        this.setSerialNumber(toCopy.getSerialNumber());
        this.setLastUser(toCopy.getLastUser());
        return this;
    }

    public static Device parse(String[] fields) {
        return Device.builder().profile(fields[1])
                .hostname(fields[2])
                .description(fields[3])
                .ipAddress(fields[4])
                .extIpAddress(fields[5])
                .lastUser(fields[6])
                .agentVersion(Integer.parseInt(fields[7]))
                .model(fields[8])
                .os(fields[9])
                .serialNumber(fields[10])
                .motherboard(fields[11]).build();
    }
}