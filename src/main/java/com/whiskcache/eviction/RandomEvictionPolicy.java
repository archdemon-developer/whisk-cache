package com.whiskcache.eviction;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomEvictionPolicy<K> implements EvictionPolicy<K> {
    private final Set<K> keys = new HashSet<>();
    private final Random random = new Random();

    @Override
    public void keyAccessed(K key) {}

    @Override
    public void keyAdded(K key) {
        keys.add(key);
    }

    @Override
    public void keyRemoved(K key) {
        keys.remove(key);
    }

    @Override
    public K evictKey() {
        if (keys.isEmpty()) {
            return null;
        }

        int index = random.nextInt(keys.size());
        return keys.stream().skip(index).findFirst().orElse(null);
    }
}