package org.lodder.subtools.sublibrary.cache;

import java.util.ArrayList;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;

/**
 * @author Crunchify.com
 */

public class InMemoryCache<K, T> {

    private long timeToLive;
    @SuppressWarnings("rawtypes")
    protected LRUMap cacheMap;

    @SuppressWarnings("rawtypes")
    public InMemoryCache(long crunchifyTimeToLive, final long crunchifyTimerInterval, int maxItems) {
        this.timeToLive = crunchifyTimeToLive * 1000;

        cacheMap = new LRUMap(maxItems);

        if (timeToLive > 0 && crunchifyTimerInterval > 0) {
            createCleanUpThread(crunchifyTimerInterval);
        }
    }

    private void createCleanUpThread(final long timerInterval) {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(timerInterval * 1000);
                } catch (InterruptedException ex) {
                }
                cleanup();
            }
        });

        t.setDaemon(true);
        t.start();
    }

    @SuppressWarnings("unchecked")
    public void put(K key, T value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheObject<K, T>(value));
        }
    }

    public boolean exists(K key) {
        synchronized (cacheMap) {
            return cacheMap.containsKey(key);
        }
    }

    @SuppressWarnings("unchecked")
    public T get(K key) {
        synchronized (cacheMap) {
            CacheObject<K, T> c = (CacheObject<K, T>) cacheMap.get(key);

            if (c == null) {
                return null;
            } else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (cacheMap) {
            MapIterator itr = cacheMap.mapIterator();

            deleteKey = new ArrayList<>(cacheMap.size() / 2 + 1);
            K key = null;
            CacheObject<K, T> c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheObject<K, T>) itr.getValue();

                if (c != null && now > timeToLive + c.created) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}
