package com.example.EWalletProject.sharding;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class HashRoutingDataSource  extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return ShardContext.getCurrentShard();
    }
}
