package com.whiskcache.config;

import com.whiskcache.persistence.PersistenceProvider;

import java.util.concurrent.TimeUnit;

public class CacheConfig<K, V> {
    private final int maxSize;
    private final long ttlCleanupInterval;
    private final TimeUnit ttlCleanupTimeUnit;
    private final long defaultTTLMillis;
    private final PersistenceProvider<K, V> persistenceProvider;

    public CacheConfig(int maxSize, long ttlCleanupInterval, TimeUnit ttlCleanupTimeUnit, long defaultTTLMillis, PersistenceProvider<K, V> persistenceProvider) {
        this.maxSize = maxSize;
        this.ttlCleanupInterval = ttlCleanupInterval;
        this.ttlCleanupTimeUnit = ttlCleanupTimeUnit;
        this.defaultTTLMillis = defaultTTLMillis;
        this.persistenceProvider = persistenceProvider;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public long getTtlCleanupInterval() {
        return ttlCleanupInterval;
    }

    public TimeUnit getTtlCleanupTimeUnit() {
        return ttlCleanupTimeUnit;
    }

    public long getDefaultTTLMillis() {
        return defaultTTLMillis;
    }

    public PersistenceProvider<K, V> getPersistenceProvider() { return persistenceProvider; }
}