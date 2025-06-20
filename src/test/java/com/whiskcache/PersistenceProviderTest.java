package com.whiskcache;

import com.whiskcache.entry.CacheEntry;
import com.whiskcache.persistence.FilePersistenceProvider;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceProviderTest {

    private FilePersistenceProvider<String, String> provider;

    @BeforeEach
    void setUp() throws Exception {
        File tempFile = File.createTempFile("cache-test", ".bin");
        tempFile.deleteOnExit();
        provider = new FilePersistenceProvider<>(tempFile.getAbsolutePath());
    }

    @Test
    void testSaveAndLoad() throws Exception {
        ConcurrentHashMap<String, CacheEntry<String>> original = new ConcurrentHashMap<>();
        original.put("key1", new CacheEntry<>("value1", 0));
        original.put("key2", new CacheEntry<>("value2", 5000));

        provider.save(original);

        Map<String, CacheEntry<String>> restored = provider.load();

        assertEquals(2, restored.size());
        assertEquals("value1", restored.get("key1").getValue());
        assertEquals("value2", restored.get("key2").getValue());
        assertFalse(restored.get("key2").isExpired());
    }

    @Test
    void testLoadNonExistentFile() throws Exception {
        File missing = new File("nonexistent-file.dat");
        FilePersistenceProvider<String, String> missingProvider = new FilePersistenceProvider<>(missing.getAbsolutePath());

        Map<String, CacheEntry<String>> result = missingProvider.load();

        assertTrue(result.isEmpty());
    }

    @Test
    void testOverwriteFile() throws Exception {
        ConcurrentHashMap<String, CacheEntry<String>> original = new ConcurrentHashMap<>();
        original.put("oldKey", new CacheEntry<>("oldVal", 0));

        provider.save(original);

        ConcurrentHashMap<String, CacheEntry<String>> updated = new ConcurrentHashMap<>();
        updated.put("newKey", new CacheEntry<>("newVal", 0));

        provider.save(updated);

        Map<String, CacheEntry<String>> restored = provider.load();

        assertEquals(1, restored.size());
        assertEquals("newVal", restored.get("newKey").getValue());
    }
}
