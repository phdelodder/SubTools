package org.lodder.subtools.sublibrary.cache;

import org.junit.Before;
import org.junit.Test;

public class InMemoryCacheTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testAddRemoveObjects() {
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

        System.out.println("6 Cache Object Added.. cache.size(): " + cache.size());
        cache.remove("IBM");
        System.out.println("One object removed.. cache.size(): " + cache.size());

        cache.put("Twitter", "Twitter");
        cache.put("SAP", "SAP");
        System.out.println("Two objects Added but reached maxItems.. cache.size(): " + cache.size());

    }

    @Test
    public void testExpiredCacheObjects() throws InterruptedException {

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

        System.out.println("Two objects are added but reached timeToLive. cache.size(): " + cache.size());
    }

    @Test
    public void testObjectsCleanupTime() throws InterruptedException {
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
