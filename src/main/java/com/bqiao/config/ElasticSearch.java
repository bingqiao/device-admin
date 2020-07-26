package com.bqiao.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.bqiao.repo.es")
public class ElasticSearch {
    @Value("${es.host:localhost:9200}")
    private String eshost;
    @Bean
    public RestHighLevelClient client() {
        log.info("Elasticsearch host: {}", eshost);
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(eshost)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    // using a different method name fails dependency test
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
