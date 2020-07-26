package com.bqiao.repo;

import com.bqiao.repo.es.DeviceDocRepo;
import com.bqiao.repo.jpa.CustomFieldRepo;
import com.bqiao.repo.jpa.DeviceRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Service
public class RepositoryService {
    @Value("${spring.datasource.url:url}")
    private String url;
    @Value("${spring.datasource.username:username}")
    private String username;
    @Value("${spring.datasource.password:password}")
    private String password;
    @Autowired
    private DeviceRepo deviceRepo;

    @Autowired
    private CustomFieldRepo customFieldRepo;

    @Autowired
    private DeviceDocRepo deviceDocRepo;

    @PostConstruct
    public void setup() {
        log.info("spring.datasource.url: {}", url);
        log.info("spring.datasource.username: {}", username);
        log.info("spring.datasource.password: {}", password);
    }
}
