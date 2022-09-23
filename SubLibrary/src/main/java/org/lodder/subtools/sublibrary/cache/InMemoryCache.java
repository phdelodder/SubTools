package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.collections4.map.LRUMap;

import com.pivovarit.function.ThrowingSupplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter(value = AccessLevel.PROTECTED)
public class InMemoryCache<K, V> {

    private final Map<K, CacheObject<V>> cacheMap;
    private final Long timeToLive;

    protected InMemoryCache(Long timeToLive, Long timerInterval, Integer maxItems) {
        if (timeToLive != null && timeToLive < 1) {
            throw new IllegalStateException("maxItems should be a positive number");
        }
        if (timerInterval != null && timerInterval < 1) {
            throw new IllegalStateException("timerInterval should be a positive number");
        }
        if (timeToLive != null && timeToLive < 1) {
            throw new IllegalStateException("timeToLive should be a positive number");
        }
        if (timeToLive == null && timerInterval != null) {
            throw new IllegalStateException("timeToLive should be specified when timerInterval is used");
        }
        if (timeToLive != null && timerInterval != null && timeToLive < timerInterval) {
            throw new IllegalStateException("timerInterval should be greater than timeToLive");
        }
        if (timerInterval != null) {
            createCleanUpThread(timerInterval);
        }
        this.timeToLive = timeToLive;
        this.cacheMap = maxItems != null ? new LRUMap<>(maxItems) : new HashMap<>();
    }

    public static InMemoryCacheBuilderKeyTypeIntf builder() {
        return new InMemoryCacheBuilder<>();
    }

    public interface InMemoryCacheBuilderKeyTypeIntf {
        <K extends Serializable> InMemoryCacheBuilderValueTypeIntf<K> keyType(Class<K> keyType);
    }

    public interface InMemoryCacheBuilderValueTypeIntf<K extends Serializable> {
        <V extends Serializable> InMemoryCacheBuilder<K, V> valueType(Class<V> valueType);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class InMemoryCacheBuilder<K extends Serializable, V extends Serializable>
            implements InMemoryCacheBuilderKeyTypeIntf, InMemoryCacheBuilderValueTypeIntf<K> {
        private Long timeToLive;
        private Long timerInterval;
        private Integer maxItems;

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Serializable> InMemoryCacheBuilder<T, V> keyType(Class<T> keyType) {
            return (InMemoryCacheBuilder<T, V>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Serializable> InMemoryCacheBuilder<K, T> valueType(Class<T> valueType) {
            return (InMemoryCacheBuilder<K, T>) this;
        }

        public InMemoryCache<K, V> build() {
            return new InMemoryCache<>(timeToLive, timerInterval, maxItems);
        }
    }

    private void createCleanUpThread(long timerInterval) {
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

    public void put(K key, V value) {
        put(key, new CacheObject<>(value));
    }

    protected void put(K key, CacheObject<V> value) {
        synchronized (cacheMap) {
            cacheMap.put(key, value);
        }
    }

    public boolean contains(K key) {
        synchronized (cacheMap) {
            return cacheMap.containsKey(key);
        }
    }

    public Optional<V> get(K key) {
        synchronized (cacheMap) {
            CacheObject<V> obj = cacheMap.get(key);
            if (obj == null) {
                return Optional.empty();
            } else {
                obj.updateLastAccessed();
                return Optional.ofNullable(obj.getValue());
            }
        }
    }

    public <X extends Exception> V getOrPut(K key, ThrowingSupplier<V, X> supplier) throws X {
        synchronized (cacheMap) {
            CacheObject<V> obj;
            if (cacheMap.containsKey(key)) {
                obj = cacheMap.get(key);
            } else {
                V value = supplier.get();
                obj = new CacheObject<>(value);
                cacheMap.put(key, obj);
            }
            if (obj == null) {
                return null;
            } else {
                obj.updateLastAccessed();
                return obj.getValue();
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

    public void cleanup() {
        long now = System.currentTimeMillis();
        synchronized (cacheMap) {
            Iterator<Entry<K, CacheObject<V>>> itr = cacheMap.entrySet().iterator();
            while (itr.hasNext()) {
                Entry<K, CacheObject<V>> entry = itr.next();
                if (now > timeToLive + entry.getValue().getCreated()) {
                    itr.remove();
                }
            }
            Thread.yield();
        }
    }
}
