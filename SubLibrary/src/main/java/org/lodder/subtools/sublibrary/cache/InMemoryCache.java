package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.tuple.Pair;

import com.pivovarit.function.ThrowingSupplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter(value = AccessLevel.PROTECTED)
public class InMemoryCache<K, V> {

    private final Map<K, CacheObject<V>> cacheMap;
    private final Long timeToLive;

    protected InMemoryCache(Long timeToLiveSeconds, Long timerIntervalSeconds, Integer maxItems) {
        if (timeToLiveSeconds != null && timeToLiveSeconds < 1) {
            throw new IllegalStateException("maxItems should be a positive number");
        }
        if (timerIntervalSeconds != null && timerIntervalSeconds < 1) {
            throw new IllegalStateException("timerInterval should be a positive number");
        }
        if (timeToLiveSeconds != null && timeToLiveSeconds < 1) {
            throw new IllegalStateException("timeToLive should be a positive number");
        }
        if (timeToLiveSeconds == null && timerIntervalSeconds != null) {
            throw new IllegalStateException("timeToLive should be specified when timerInterval is used");
        }
        if (timeToLiveSeconds != null && timerIntervalSeconds != null && timeToLiveSeconds < timerIntervalSeconds) {
            throw new IllegalStateException("timerInterval should be greater than timeToLive");
        }
        if (timerIntervalSeconds != null) {
            createCleanUpThread(timerIntervalSeconds * 1000);
        }
        this.timeToLive = timeToLiveSeconds * 1000;
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
                    Thread.sleep(timerInterval);
                } catch (InterruptedException ex) {
                }
                cleanup();
            }
        });

        t.setDaemon(true);
        t.start();
    }

    public void put(K key, V value) {
        put(key, new ExpiringCacheObject<>(value));
    }

    public void put(K key, V value, long timeToLive) {
        put(key, new TemporaryCacheObject<>(timeToLive, value));
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

    public boolean isTemporaryObject(K key) {
        synchronized (cacheMap) {
            CacheObject<V> obj = cacheMap.get(key);
            if (obj == null) {
                return false;
            } else {
                return obj instanceof TemporaryCacheObject<?>;
            }
        }
    }

    public boolean isTemporaryExpired(K key) {
        synchronized (cacheMap) {
            CacheObject<V> obj = cacheMap.get(key);
            if (obj == null) {
                return false;
            } else {
                return obj instanceof TemporaryCacheObject<?> tempCacheObject && tempCacheObject.isExpired();
            }
        }
    }

    public OptionalLong getTemporaryTimeToLive(K key) {
        synchronized (cacheMap) {
            CacheObject<V> obj = cacheMap.get(key);
            if (obj == null) {
                return OptionalLong.empty();
            } else {
                return obj instanceof TemporaryCacheObject<?> tempCacheObject ? OptionalLong.of(tempCacheObject.getTimeToLive())
                        : OptionalLong.empty();
            }
        }
    }

    public <X extends Exception> V getOrPut(K key, ThrowingSupplier<V, X> supplier) throws X {
        boolean containsKey = false;
        CacheObject<V> obj = null;
        synchronized (cacheMap) {
            if (cacheMap.containsKey(key)) {
                containsKey = true;
                obj = cacheMap.get(key);
            }
        }
        if (!containsKey) {
            V value = supplier.get();
            obj = new ExpiringCacheObject<>(value);
            synchronized (cacheMap) {
                cacheMap.put(key, obj);
            }
        }
        if (obj == null) {
            return null;
        } else {
            obj.updateLastAccessed();
            return obj.getValue();
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
        synchronized (cacheMap) {
            Iterator<Entry<K, CacheObject<V>>> itr = cacheMap.entrySet().iterator();
            while (itr.hasNext()) {
                Entry<K, CacheObject<V>> entry = itr.next();
                if (entry.getValue().isExpired(timeToLive)) {
                    itr.remove();
                }
            }

            // TODO clean disk cache?
            // TODO remove temporary from diskcahe, but not memorycache
            Thread.yield();
        }
    }

    public List<Pair<K, V>> getEntries() {
        return getEntries(null);
    }

    public List<Pair<K, V>> getEntries(Predicate<K> keyFilter) {
        synchronized (cacheMap) {
            return cacheMap.entrySet().stream().filter(entry -> keyFilter == null || keyFilter.test(entry.getKey()))
                    .map(entry -> Pair.of(entry.getKey(), entry.getValue().getValue())).toList();
        }
    }

    public void deleteEntries(Predicate<K> keyFilter) {
        synchronized (cacheMap) {
            Iterator<Entry<K, CacheObject<V>>> iterator = cacheMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<K, CacheObject<V>> entry = iterator.next();
                if (keyFilter == null || keyFilter.test(entry.getKey())) {
                    iterator.remove();
                }
            }
        }
    }
}
