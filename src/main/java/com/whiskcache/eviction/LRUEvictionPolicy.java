package com.whiskcache.eviction;

import java.util.LinkedHashSet;

public class LRUEvictionPolicy <K> implements EvictionPolicy<K> {

    private final LinkedHashSet<K> order = new LinkedHashSet<>();

    @Override
    public void keyAccessed(K key) {
        order.remove(key);
        order.add(key);
    }

    @Override
    public void keyAdded(K key) {
        keyAccessed(key);
    }

    @Override
    public void keyRemoved(K key) {
        order.remove(key);
    }

    @Override
    public K evictKey() {
        return order.iterator().hasNext() ? order.iterator().next() : null;
    }
}
