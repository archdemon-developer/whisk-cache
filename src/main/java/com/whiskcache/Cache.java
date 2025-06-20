package com.whiskcache;

import com.whiskcache.stats.CacheStats;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    int size();
    void clear();
    CacheStats stats();
    boolean containsKey(K key);
    V getOrDefault(K key, V defaultValue);
    V putIfAbsent(K key, V value);
    Set<Map.Entry<K, V>> entrySet();
    void evict(K key);
    Set<K> keySet();
    Collection<V> values();
    void shutdown();
}
