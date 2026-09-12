package com.example.archmind.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.Neo4jClient;

/**
 * Neo4j 图访问配置。
 * Boot4 的 Neo4jAutoConfiguration 已按 spring.neo4j.* 自动创建 Driver bean，
 * 这里只在其上暴露一个同步 Neo4jClient 供图写入使用（SDN 的 client 不会自动配置）。
 */
@Configuration
public class Neo4jGraphConfig {

    @Bean
    public Neo4jClient neo4jClient(Driver driver) {
        return Neo4jClient.create(driver);
    }
}
