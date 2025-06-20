package com.whiskcache;

import com.whiskcache.eviction.*;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EvictionPolicyTest {

    @Test
    void testLRUEviction() {
        LRUEvictionPolicy<String> policy = new LRUEvictionPolicy<>();
        policy.keyAdded("A");
        policy.keyAdded("B");
        policy.keyAccessed("A"); // A becomes most recently used
        policy.keyAdded("C");

        assertEquals("B", policy.evictKey()); // B is now least recently used
    }

    @Test
    void testLFUEviction() {
        LFUEvictionPolicy<String> policy = new LFUEvictionPolicy<>();
        policy.keyAdded("A"); // freq 1
        policy.keyAdded("B"); // freq 1
        policy.keyAccessed("A"); // freq A = 2
        policy.keyAdded("C"); // freq 1

        assertEquals("B", policy.evictKey()); // B has lowest frequency
    }

    @Test
    void testFIFOEviction() {
        FIFOEvictionPolicy<String> policy = new FIFOEvictionPolicy<>();
        policy.keyAdded("X");
        policy.keyAdded("Y");
        policy.keyAdded("Z");

        assertEquals("X", policy.evictKey());
    }

    @Test
    void testNoEvictionThrows() {
        NoEvictionPolicy<String> policy = new NoEvictionPolicy<>();
        assertThrows(IllegalStateException.class, policy::evictKey);
    }

    @Test
    void testRandomEvictionReturnsKnownKey() {
        RandomEvictionPolicy<String> policy = new RandomEvictionPolicy<>();
        policy.keyAdded("K1");
        policy.keyAdded("K2");
        policy.keyAdded("K3");

        String evicted = policy.evictKey();
        assertTrue(Set.of("K1", "K2", "K3").contains(evicted));
    }

    @Test
    void testEvictionAfterRemoval() {
        LRUEvictionPolicy<String> policy = new LRUEvictionPolicy<>();
        policy.keyAdded("one");
        policy.keyAdded("two");
        policy.keyRemoved("one");

        assertEquals("two", policy.evictKey());
    }
}
