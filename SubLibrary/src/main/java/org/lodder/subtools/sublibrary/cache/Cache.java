package org.lodder.subtools.sublibrary.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.pivovarit.function.ThrowingSupplier;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(value = AccessLevel.PROTECTED)
public abstract class Cache<K, V> {

    private final Map<K, CacheObject<V>> cacheMap;
    private final Integer maxItems;

    protected Cache(Integer maxItems) {
        this.maxItems = maxItems;
        this.cacheMap = maxItems != null ? new LRUMap<>(maxItems) : new HashMap<>();
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

    public List<Pair<K, V>> getEntries() {
        return getEntries(null);
    }

    public List<Pair<K, V>> getEntries(Predicate<K> keyFilter) {
        synchronized (cacheMap) {
            return getEntryStream(keyFilter).map(entry -> Pair.of(entry.getKey(), entry.getValue().getValue())).toList();
        }
    }

    public Stream<Entry<K, CacheObject<V>>> getEntryStream(Predicate<K> keyFilter) {
        synchronized (cacheMap) {
            return cacheMap.entrySet().stream().filter(entry -> keyFilter == null || keyFilter.test(entry.getKey()));
        }
    }

    public void deleteEntries(Predicate<K> keyFilter) {
        synchronized (cacheMap) {
            getEntryStream(keyFilter).toList().forEach(entry -> remove(entry.getKey()));
        }
    }
}
