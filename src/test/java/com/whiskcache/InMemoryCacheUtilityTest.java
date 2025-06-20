package com.whiskcache;
import com.whiskcache.config.CacheConfig;
import com.whiskcache.eviction.LRUEvictionPolicy;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCacheUtilityTest {

    private InMemoryCache<String, String> cache;

    @BeforeEach
    void setup() {
        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                5,
                TimeUnit.SECONDS,
                0,
                null
        );
        cache = new InMemoryCache<>(config, new LRUEvictionPolicy<>());
    }

    @AfterEach
    void cleanup() {
        cache.shutdown();
    }

    @Test
    void testContainsKey() {
        cache.put("a", "apple");
        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
    }

    @Test
    void testGetOrDefault() {
        cache.put("a", "apple");
        assertEquals("apple", cache.getOrDefault("a", "default"));
        assertEquals("default", cache.getOrDefault("b", "default"));
    }

    @Test
    void testPutIfAbsent() {
        cache.put("a", "apple");
        cache.putIfAbsent("a", "apricot"); // shouldn't overwrite
        assertEquals("apple", cache.get("a"));

        cache.putIfAbsent("b", "banana");
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testEvict() {
        cache.put("a", "apple");
        assertTrue(cache.containsKey("a"));
        cache.evict("a");
        assertFalse(cache.containsKey("a"));
        assertEquals(1, cache.stats().getEvictions());
    }

    @Test
    void testKeySetValuesEntrySet() {
        cache.put("a", "apple");
        cache.put("b", "banana");

        Set<String> keys = cache.keySet();
        Collection<String> values = cache.values();
        Set<Map.Entry<String, String>> entries = cache.entrySet();

        assertEquals(Set.of("a", "b"), keys);
        assertTrue(values.contains("apple") && values.contains("banana"));
        assertEquals(2, entries.size());

        Map<String, String> asMap = new HashMap<>();
        for (Map.Entry<String, String> e : entries) {
            asMap.put(e.getKey(), e.getValue());
        }
        assertEquals("apple", asMap.get("a"));
        assertEquals("banana", asMap.get("b"));
    }
}
