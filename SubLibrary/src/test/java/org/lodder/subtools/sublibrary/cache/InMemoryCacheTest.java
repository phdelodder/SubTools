package org.lodder.subtools.sublibrary.cache;

import static manifold.science.measures.TimeUnit.*;
import static manifold.science.util.UnitConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.lodder.subtools.sublibrary.util.Sleep.*;

import manifold.science.measures.Time;
import org.junit.jupiter.api.Test;

class InMemoryCacheTest {

    @Test
    void testAddRemoveObjects() {
        InMemoryCache<String, String> cache =
            new InMemoryCache<>(
                keyType:String.class,
                valueType:String.class,
                timeToLive:200 ms,
                timerInterval:100 ms,
                maxItems:6);

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
    void testExpiredCacheObjects() {

        InMemoryCache<String, String> cache =
            new InMemoryCache<>(
                keyType:String.class,
                valueType:String.class,
                timeToLive:1 ms,
                timerInterval:1 ms,
                maxItems:10);

        cache.put("eBay", "eBay");
        cache.put("Paypal", "Paypal");
        // Adding 3 seconds sleep.. Both above objects will be removed from
        // Cache because of timeToLiveInSeconds value

        sleep(3 Second);

        assertThat(cache.size()).as("Cache should not contain items that are expired").isEqualTo(0);
    }

    @Test
    void testObjectsCleanupTime() {
        int size = 500;

        InMemoryCache<String, String> cache =
            new InMemoryCache<>(
                keyType:String.class,
                valueType:String.class,
                timeToLive:100 ms,
                timerInterval:100 ms,
                maxItems:500);

        for (int i = 0; i < size; i++) {
            String value = Integer.toString(i);
            cache.put(value, value);
        }

        sleep(200 ms);

        Time start = Time.now();
        cache.cleanup();
        Time duration = Time.now() - start;

        System.out.println("Cleanup duration for $size objects is $duration");
    }

}
