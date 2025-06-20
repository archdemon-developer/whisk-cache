package com.whiskcache.eviction;

public class NoEvictionPolicy<K> implements EvictionPolicy<K> {
    @Override public void keyAccessed(K key) {}
    @Override public void keyAdded(K key) {}
    @Override public void keyRemoved(K key) {}
    @Override public K evictKey() {
        throw new IllegalStateException("Eviction not supported by NoEvictionPolicy");
    }
}