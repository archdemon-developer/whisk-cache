package com.whiskcache;

import com.whiskcache.config.CacheConfig;
import com.whiskcache.eviction.LRUEvictionPolicy;
import com.whiskcache.persistence.FilePersistenceProvider;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCacheWarmBootTest {

    private File tempFile;

    @BeforeEach
    void setup() throws Exception {
        tempFile = File.createTempFile("cache-warmboot", ".bin");
        tempFile.deleteOnExit();
    }

    @Test
    void testWarmBootFromDisk() throws Exception {
        String path = tempFile.getAbsolutePath();

        // First: Create cache and populate it
        FilePersistenceProvider<String, String> provider = new FilePersistenceProvider<>(path);
        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                1,
                TimeUnit.SECONDS,
                0,
                provider
        );
        InMemoryCache<String, String> cache = new InMemoryCache<>(config, new LRUEvictionPolicy<>());
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.shutdown(); // Triggers save to disk

        // Second: Create a new cache instance (simulating a reboot)
        InMemoryCache<String, String> warmBootCache = new InMemoryCache<>(config, new LRUEvictionPolicy<>());
        String val1 = warmBootCache.get("key1");
        String val2 = warmBootCache.get("key2");

        assertEquals("value1", val1);
        assertEquals("value2", val2);
        assertTrue(warmBootCache.containsKey("key1"));
        assertTrue(warmBootCache.containsKey("key2"));

        warmBootCache.shutdown();
    }
}
