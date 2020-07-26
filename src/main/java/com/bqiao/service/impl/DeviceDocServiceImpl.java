package com.bqiao.service.impl;

import com.bqiao.domain.Device;
import com.bqiao.domain.DeviceDoc;
import com.bqiao.domain.DeviceDocField;
import com.bqiao.repo.RepositoryService;
import com.bqiao.service.DeviceDocService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.bqiao.domain.DeviceDocField.CUSTOMFIELDS;
import static com.bqiao.domain.DeviceDocField.PROFILE;
import static java.util.stream.Collectors.toMap;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Slf4j
@Component
public class DeviceDocServiceImpl implements DeviceDocService {
    private final RepositoryService repositoryService;
    private final ElasticsearchOperations esTemplate;

    public DeviceDocServiceImpl(
            RepositoryService repositoryService,
            ElasticsearchOperations esTemplate) {
        this.repositoryService = repositoryService;
        this.esTemplate = esTemplate;
    }
    @Override
    public DeviceDoc createDeviceDoc(Device device) {
        DeviceDoc doc = getDeviceDoc(device);
        DeviceDoc created = repositoryService.getDeviceDocRepo().save(doc);
        log.debug(created.toString());
        return created;
    }

    private DeviceDoc getDeviceDoc(Device device) {
        return DeviceDoc.builder().id(device.getId())
                    .agentVer(device.getAgentVersion())
                    .desc(device.getDescription())
                    .host(device.getHostname())
                    .ip(device.getIpAddress())
                    .extIp(device.getExtIpAddress())
                    .lastUser(device.getLastUser())
                    .model(device.getModel())
                    .mb(device.getMotherboard())
                    .os(device.getOs())
                    .sn(device.getSerialNumber())
                    .profile(device.getProfile())
                    .customFields(findCustomFields(device))
                    .created(device.getCreated())
                    .modified(device.getModified())
                    .build();
    }
    private Map<String, String> findCustomFields(Device device) {
        Map<Integer, String> fields = device.getCustomFields();
        return fields.keySet()
                .stream().collect(toMap(String::valueOf, fields::get));
    }

    @Override
    public List<DeviceDoc> createDeviceDocs(List<Device> devices) {
        List<DeviceDoc> docs = devices.stream().map(this::getDeviceDoc).collect(Collectors.toList());
        List<DeviceDoc> created = StreamSupport
                .stream(repositoryService.getDeviceDocRepo().saveAll(docs).spliterator(), false)
                .collect(Collectors.toList());
        log.debug("created: {}", created.size());
        return created;
    }

    @Override
    public List<DeviceDoc> updateDeviceDocs(List<Device> devices) {
        if (CollectionUtils.isEmpty(devices)) {
            return Collections.emptyList();
        }
        Map<String, Device> deviceMap = devices.stream().collect(Collectors.toMap(Device::getId, Function.identity()));
        List<String> ids = devices.stream().map(Device::getId).collect(Collectors.toList());
        List<DeviceDoc> docsToUpdate = findAllByIds(ids)
                .map(doc -> doc.copy(deviceMap.get(doc.getId()))).collect(Collectors.toList());
        return saveAll(docsToUpdate);
    }

    @Override
    public void deleteById(String id) {
        repositoryService.getDeviceDocRepo().deleteById(id);
    }

    @Override
    public void deleteByIds(Optional<List<String>> ids) {
        if (ids.isEmpty()) {
            repositoryService.getDeviceDocRepo().deleteAll();
        } else {
            List<DeviceDoc> toDelete = findAllByIds(ids.get()).collect(Collectors.toList());
            repositoryService.getDeviceDocRepo().deleteAll(toDelete);
        }
    }

    private List<DeviceDoc> saveAll(List<DeviceDoc> docsToUpdate) {
        return StreamSupport
                .stream(repositoryService.getDeviceDocRepo().saveAll(docsToUpdate).spliterator(), false)
                .collect(Collectors.toList());
    }

    private Stream<DeviceDoc> findAllByIds(List<String> ids) {
        return StreamSupport
                        .stream(repositoryService.getDeviceDocRepo().findAllById(ids).spliterator(), false);
    }

    private Stream<DeviceDoc> findAll() {
        return StreamSupport
                .stream(repositoryService.getDeviceDocRepo().findAll().spliterator(), false);
    }

    private Stream<DeviceDoc> findAll(List<String> ids) {
        return StreamSupport
                .stream(repositoryService.getDeviceDocRepo().findAllById(ids).spliterator(), false);
    }

    @Override
    public void delete(List<Device> devices) {
        if (!CollectionUtils.isEmpty(devices)) {
            List<String> ids = devices.stream().map(Device::getId).collect(Collectors.toList());
            List<DeviceDoc> toDelete = findAllByIds(ids).collect(Collectors.toList());
            repositoryService.getDeviceDocRepo().deleteAll(toDelete);
        }
    }

    @Override
    public List<DeviceDoc> findDevicesByDesc(String desc) {
        return repositoryService.getDeviceDocRepo().findByDesc(desc);
    }

    @Override
    public List<DeviceDoc> findDeviceDocs(Optional<List<String>> ids) {
        List<DeviceDoc> deviceDocs;
        if (ids.isEmpty()) {
            deviceDocs = this.findAll().collect(Collectors.toList());
        } else {
            deviceDocs = this.findAll(ids.get()).collect(Collectors.toList());
        }
        return deviceDocs;
    }

    @Override
    public List<DeviceDoc> findDevices(Map<String, String> params) {
        if (params == null) {
            params = Collections.emptyMap();
        }
        BoolQueryBuilder builder = boolQuery();
        params.forEach((key, value) -> {
            DeviceDocField field = DeviceDocField.parseText(key);
            if (field != null) {
                String f = field.getText();
                if (field == CUSTOMFIELDS) {
                    f = CUSTOMFIELDS.getText() + "." + key.substring(2);
                }
                builder.must(regexpQuery(f, ".*" + value.toLowerCase() + ".*"));
            }
        });

        String order = params.get(SEARCH_PARAM_ORDER);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        List<String> sorts = Arrays.asList(params.getOrDefault(SEARCH_PARAM_SORT, "").split(",", 15));
        if (sorts.size() == 0) {
            sorts.add(PROFILE.getText());
        }
        sorts.forEach(s -> {
            DeviceDocField field = DeviceDocField.parseText(s);
            if (field != null && field.isSortable()) {
                SortOrder sortOrder = SortOrder.DESC.name().equalsIgnoreCase(order) ? SortOrder.DESC : SortOrder.ASC;
                queryBuilder.withSort(SortBuilders.fieldSort(field.getText()).order(sortOrder));
            }
        });

        Query searchQuery = queryBuilder
                .withQuery(builder)
                .build();
        SearchHits<DeviceDoc> devices =
                esTemplate.search(searchQuery, DeviceDoc.class, IndexCoordinates.of("device"));
        return devices.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
