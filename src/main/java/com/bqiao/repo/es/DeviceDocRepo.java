package com.bqiao.repo.es;

import com.bqiao.domain.DeviceDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceDocRepo extends ElasticsearchRepository<DeviceDoc, String> {
    List<DeviceDoc> findByDesc(String keyword);
    List<DeviceDoc> findByProfileAndHostAndDesc(String profile, String host, String desc);
}
