package com.whiskcache;

import com.whiskcache.config.CacheConfig;
import com.whiskcache.entry.CacheEntry;
import com.whiskcache.eviction.EvictionPolicy;
import com.whiskcache.stats.CacheStats;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class InMemoryCache<K, V> implements Cache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cacheStore = new ConcurrentHashMap<>();
    private final EvictionPolicy<K> evictionPolicy;
    private final ScheduledExecutorService cleaner;
    private final CacheConfig<K, V> cacheConfig;
    private final CacheStats stats = new CacheStats();

    public InMemoryCache(CacheConfig<K, V> cacheConfig, EvictionPolicy<K> evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
        this.cleaner = Executors.newSingleThreadScheduledExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(true);
                    thread.setName("TTL-Cleanup-Thread");
                    return thread;
                }
        );
        this.cacheConfig = cacheConfig;
        loadPersistedData();
        startCleanupTask();
    }


    private void loadPersistedData() {
        if (cacheConfig.getPersistenceProvider() != null) {
            try {
                Map<K, CacheEntry<V>> loaded = cacheConfig.getPersistenceProvider().load();
                cacheStore.putAll(loaded);
                loaded.keySet().forEach(evictionPolicy::keyAdded);
                System.out.println("Cache warm boot loaded " + loaded.size() + " entries");
            } catch (Exception e) {
                System.err.println("Failed to load persisted cache: " + e.getMessage());
            }
        }
    }


    private void startCleanupTask() {
        cleaner.scheduleAtFixedRate(() -> {
            try {
                Iterator<Map.Entry<K, CacheEntry<V>>> iterator = cacheStore.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry<K, CacheEntry<V>> entry = iterator.next();
                    if(entry.getValue().isExpired()) {
                        iterator.remove();
                        evictionPolicy.keyRemoved(entry.getKey());
                    }
                }
            } catch(Exception ex) {
                System.err.println("TTL Cleanup Error");
            }
        }, cacheConfig.getTtlCleanupInterval(),
                cacheConfig.getTtlCleanupInterval(),
                cacheConfig.getTtlCleanupTimeUnit());
    }

    @Override
    public V get(K key) {
        CacheEntry<V> entry = cacheStore.get(key);
        if(entry == null || entry.isExpired()) {
            cacheStore.remove(key);
            evictionPolicy.keyRemoved(key);
            stats.recordMiss();
            return null;
        }
        evictionPolicy.keyAccessed(key);
        stats.recordHit();
        return entry.getValue();
    }

    @Override
    public void put(K key, V value) {
        long ttlMillis = cacheConfig.getDefaultTTLMillis();
        if(cacheStore.size() >= cacheConfig.getMaxSize()) {
           K evictedKey = evictionPolicy.evictKey();
           if(evictedKey != null) {
               cacheStore.remove(evictedKey);
               evictionPolicy.keyRemoved(evictedKey);
               stats.recordEviction();
           }
        }
        cacheStore.put(key, new CacheEntry<>(value, ttlMillis));
        evictionPolicy.keyAdded(key);
    }

    @Override
    public void remove(K key) {
        cacheStore.remove(key);
        evictionPolicy.keyRemoved(key);
    }

    @Override
    public int size() {
        return cacheStore.size();
    }

    @Override
    public void clear() {
        cacheStore.clear();
    }

    @Override
    public void shutdown() {
        cleaner.shutdown();
        if (cacheConfig.getPersistenceProvider() != null) {
            try {
                cacheConfig.getPersistenceProvider().save(cacheStore);
            } catch (IOException e) {
                System.err.println("Failed to persist cache: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        CacheEntry<V> entry = cacheStore.get(key);
        return entry != null && !entry.isExpired();
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        CacheEntry<V> existing = cacheStore.get(key);
        if(existing != null && !existing.isExpired()) {
            return existing.getValue();
        }
        put(key, value);
        return null;
    }

    @Override
    public Set<K> keySet() {
        return cacheStore.keySet();
    }

    @Override
    public Collection<V> values() {
        return cacheStore.values().stream().filter(vCacheEntry -> !vCacheEntry.isExpired())
                .map(CacheEntry::getValue)
                .toList();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return cacheStore.entrySet().stream()
                .filter(e -> !e.getValue().isExpired())
                .map(e -> Map.entry(e.getKey(), e.getValue().getValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public void evict(K key) {
        cacheStore.remove(key);
        evictionPolicy.keyRemoved(key);
        stats.recordEviction();
    }

    @Override
    public CacheStats stats() {
        return stats;
    }

    public String toJsonSnapshot() {
        return cacheStore.entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
