package com.whiskcache.eviction;

import java.util.LinkedList;
import java.util.Queue;

public class FIFOEvictionPolicy<K> implements EvictionPolicy<K> {
    private final Queue<K> store = new LinkedList<>();

    @Override
    public void keyAccessed(K key) {
        //No-op: access doesnt matter in fifo
    }

    @Override
    public void keyAdded(K key) {
        store.offer(key);
    }

    @Override
    public void keyRemoved(K key) {
        store.remove(key);
    }

    @Override
    public K evictKey() {
        return store.peek();
    }

}
