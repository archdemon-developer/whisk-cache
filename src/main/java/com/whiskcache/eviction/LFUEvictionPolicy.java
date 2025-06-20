package com.whiskcache.eviction;

import java.util.HashMap;
import java.util.Map;

public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final Map<K, Integer> frequencyMap = new HashMap<>();

    @Override
    public void keyAccessed(K key) {
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
    }

    @Override
    public void keyAdded(K key) {
        frequencyMap.put(key, 1);
    }

    @Override
    public void keyRemoved(K key) {
        frequencyMap.remove(key);
    }

    @Override
    public K evictKey() {
        return frequencyMap
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

}
