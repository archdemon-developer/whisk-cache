package com.whiskcache;

import com.whiskcache.config.CacheConfig;
import com.whiskcache.eviction.LRUEvictionPolicy;
import com.whiskcache.stats.CacheStats;
import org.junit.jupiter.api.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCacheTest {

    private InMemoryCache<String, String> cache;

    @BeforeEach
    void setup() {
        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                1,
                TimeUnit.SECONDS,
                200,
                null // No persistence
        );
        cache = new InMemoryCache<>(config, new LRUEvictionPolicy<>());
    }

    @AfterEach
    void teardown() {
        cache.shutdown();
    }

    @Test
    void testPutAndGet() {
        cache.put("a", "apple");
        assertEquals("apple", cache.get("a"));
    }

    @Test
    void testContainsKey() {
        cache.put("b", "banana");
        assertTrue(cache.containsKey("b"));
        assertFalse(cache.containsKey("x"));
    }

    @Test
    void testPutIfAbsent() {
        cache.put("a", "apple");
        cache.putIfAbsent("a", "apricot");
        assertEquals("apple", cache.get("a"));

        cache.putIfAbsent("b", "banana");
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testGetOrDefault() {
        cache.put("a", "apple");
        assertEquals("apple", cache.getOrDefault("a", "none"));
        assertEquals("none", cache.getOrDefault("x", "none"));
    }

    @Test
    void testEvictionBySize() {
        for (int i = 0; i < 11; i++) {
            cache.put("key" + i, "val" + i);
        }
        assertEquals(10, cache.size());
    }

    @Test
    void testTTLExpiration() throws InterruptedException {
        cache.put("temp", "value");
        Thread.sleep(300);
        assertNull(cache.get("temp"));
    }

    @Test
    void testStatsHitMissEvict() {
        cache.put("a", "apple");
        cache.get("a"); // hit
        cache.get("b"); // miss
        cache.evict("a"); // eviction
        CacheStats stats = cache.stats();

        assertEquals(1, stats.getHits());
        assertEquals(1, stats.getMisses());
        assertEquals(1, stats.getEvictions());
    }

    @Test
    void testKeySetAndEntrySet() {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertTrue(cache.keySet().contains("a"));
        assertEquals(2, cache.entrySet().size());
    }
}
