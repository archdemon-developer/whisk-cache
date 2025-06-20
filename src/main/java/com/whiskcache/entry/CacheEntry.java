package com.whiskcache.entry;

import java.io.Serial;
import java.io.Serializable;

public class CacheEntry<V> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final V value;
    private final long creationTime;
    private final long ttlMillis;

    public CacheEntry(V value, long ttlMillis) {
        this.value = value;
        this.creationTime = System.currentTimeMillis();
        this.ttlMillis = ttlMillis;
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        return ttlMillis > 0 && (System.currentTimeMillis() - creationTime) >= ttlMillis;
    }
}
