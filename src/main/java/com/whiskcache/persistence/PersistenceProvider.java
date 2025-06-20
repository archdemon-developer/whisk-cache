package com.whiskcache.persistence;

import com.whiskcache.entry.CacheEntry;

import java.io.IOException;
import java.util.Map;

public interface PersistenceProvider<K, V> {
    void save(Map<K, CacheEntry<V>> data) throws IOException;
    Map<K, CacheEntry<V>> load() throws IOException, ClassNotFoundException;
}
