package com.bqiao.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Map;

import static org.springframework.data.elasticsearch.annotations.FieldType.Long;
import static org.springframework.data.elasticsearch.annotations.FieldType.*;

@Data
@Builder
@ToString(callSuper=true, includeFieldNames=true)
@Document(indexName = "device")
public class DeviceDoc {
    @Id
    private String id;

    @Field(type = Keyword)
    private String profile;
    @Field(type = Text)
    private String host;
    @Field(type = Text)
    private String desc;
    @Field(type = Text)
    private String ip;
    @Field(type = Text)
    private String extIp;
    @Field(type = Text)
    private String lastUser;
    @Field(type = Integer)
    private int agentVer;
    @Field(type = Text)
    private String model;
    @Field(type = Text)
    private String os;
    @Field(type = Text)
    private String sn;
    @Field(type = Text)
    private String mb;
    @Field(type = Flattened)
    private Map<String, String> customFields;

    @Field(type = Long)
    private long created;

    @Field(type = Long)
    private long modified;

    public DeviceDoc copy(Device toCopy) {
        if (toCopy == null) {
            return this;
        }
        this.setProfile(toCopy.getProfile());
        this.setAgentVer(toCopy.getAgentVersion());
        this.setDesc(toCopy.getDescription());
        this.setExtIp(toCopy.getExtIpAddress());
        this.setHost(toCopy.getHostname());
        this.setIp(toCopy.getIpAddress());
        this.setModel(toCopy.getModel());
        this.setMb(toCopy.getMotherboard());
        this.setOs(toCopy.getOs());
        this.setSn(toCopy.getSerialNumber());
        this.setLastUser(toCopy.getLastUser());
        return this;
    }
}
