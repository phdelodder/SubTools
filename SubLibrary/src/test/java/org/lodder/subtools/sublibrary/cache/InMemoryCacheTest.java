package org.lodder.subtools.sublibrary.cache;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InMemoryCacheTest {

    @Test
    void testAddRemoveObjects() {
        InMemoryCache<String, String> cache =
                InMemoryCache.builder().keyType(String.class).valueType(String.class)
                        .timeToLive(200L)
                        .timerInterval(100L)
                        .maxItems(6)
                        .build();

        cache.put("eBay", "eBay");
        cache.put("Paypal", "Paypal");
        cache.put("Google", "Google");
        cache.put("Microsoft", "Microsoft");
        cache.put("IBM", "IBM");
        cache.put("Facebook", "Facebook");

        assertThat(cache.size()).as("Cache should contain 6 entries").isEqualTo(6);
        cache.remove("IBM");
        assertThat(cache.size()).as("After deletion, cache should contain 5 entries").isEqualTo(5);

        cache.put("Twitter", "Twitter");
        cache.put("SAP", "SAP");

        assertThat(cache.size()).as("Cache should not contain more elements than it max defined size").isEqualTo(6);
    }

    @Test
    void testExpiredCacheObjects() throws InterruptedException {

        InMemoryCache<String, String> cache =
                InMemoryCache.builder().keyType(String.class).valueType(String.class)
                        .timeToLive(1L)
                        .timerInterval(1L)
                        .maxItems(10)
                        .build();

        cache.put("eBay", "eBay");
        cache.put("Paypal", "Paypal");
        // Adding 3 seconds sleep.. Both above objects will be removed from
        // Cache because of timeToLiveInSeconds value
        Thread.sleep(3000);

        assertThat(cache.size()).as("Cache should not contain items that are expired").isEqualTo(0);
    }

    @Test
    void testObjectsCleanupTime() throws InterruptedException {
        int size = 500000;

        InMemoryCache<String, String> cache =
                InMemoryCache.builder().keyType(String.class).valueType(String.class)
                        .timeToLive(100L)
                        .timerInterval(100L)
                        .maxItems(500000)
                        .build();

        for (int i = 0; i < size; i++) {
            String value = Integer.toString(i);
            cache.put(value, value);
        }

        Thread.sleep(200);

        long start = System.currentTimeMillis();
        cache.cleanup();
        double finish = (System.currentTimeMillis() - start) / 1000.0;

        System.out.println("Cleanup times for " + size + " objects are " + finish + " s");
    }

}
