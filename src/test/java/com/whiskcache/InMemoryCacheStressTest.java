package com.whiskcache;

import com.whiskcache.config.CacheConfig;
import com.whiskcache.eviction.LFUEvictionPolicy;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCacheStressTest {

    @Test
    void testConcurrentAccessAndTTL() throws InterruptedException {
        int threadCount = 20;
        int iterationsPerThread = 1000;
        int maxSize = 100;
        long ttlMillis = 200;

        CacheConfig<String, String> config = new CacheConfig<>(
                maxSize,
                1,
                TimeUnit.SECONDS,
                ttlMillis,
                null
        );

        InMemoryCache<String, String> cache = new InMemoryCache<>(config, new LFUEvictionPolicy<>());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Random random = new Random();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        int keyNum = random.nextInt(200); // > maxSize to trigger eviction
                        String key = "key" + keyNum;
                        String value = "val" + keyNum;

                        int action = random.nextInt(3);
                        switch (action) {
                            case 0 -> cache.put(key, value);       // write
                            case 1 -> cache.get(key);                          // read
                            case 2 -> cache.remove(key);                       // delete
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await();
        executor.shutdown();
        cache.shutdown(); // Clean up resources

        // Post-stress assertions
        assertTrue(cache.size() <= maxSize, "Cache size exceeded max limit");
        assertNotNull(cache.stats());
        System.out.println("Stress test completed.");
        System.out.println(cache.stats());
    }
}
