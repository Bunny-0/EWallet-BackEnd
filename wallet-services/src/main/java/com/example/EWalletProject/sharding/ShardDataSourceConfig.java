package com.example.EWalletProject.sharding;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ShardDataSourceConfig {

    @Bean
    public DataSource dataSource(){
        Map<Object,Object> map=new HashMap<>();
        map.put(0, shard0());
        map.put(1, shard1());
        map.put(2, shard2());
        map.put(3, shard3());
        HashRoutingDataSource routingDataSource=new HashRoutingDataSource();
        routingDataSource.setTargetDataSources(map);
        routingDataSource.setDefaultTargetDataSource(shard0());
        return routingDataSource;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.shard0")
    public DataSource shard0() { return DataSourceBuilder.create().build(); }

    @Bean
    @ConfigurationProperties("spring.datasource.shard1")
    public DataSource shard1() { return DataSourceBuilder.create().build(); }

    @Bean
    @ConfigurationProperties("spring.datasource.shard2")
    public DataSource shard2() { return DataSourceBuilder.create().build(); }

    @Bean
    @ConfigurationProperties("spring.datasource.shard3")
    public DataSource shard3() { return DataSourceBuilder.create().build(); }
}
