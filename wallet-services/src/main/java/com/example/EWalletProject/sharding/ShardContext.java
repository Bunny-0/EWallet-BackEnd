package com.example.EWalletProject.sharding;

public class ShardContext {

    public static final ThreadLocal<Integer> CURRENT_SHARD = new ThreadLocal<>();

    public static void setCurrentShard(int shardId) {
        CURRENT_SHARD.set(shardId);
    }
    public static Integer getCurrentShard() {
        return CURRENT_SHARD.get();
    }

    public static void clear() {
        CURRENT_SHARD.remove();
    }
}
