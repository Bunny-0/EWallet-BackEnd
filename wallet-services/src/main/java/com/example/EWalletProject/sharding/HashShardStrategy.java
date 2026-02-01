package com.example.EWalletProject.sharding;

import org.springframework.beans.factory.annotation.Value;

public class HashShardStrategy {

    @Value("${spring.shard.count}")
    private int countOfShards;
    public int getShardId(String key){
        int hash = Math.abs(key.hashCode());
        return hash % countOfShards;
    }
}
