package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter(value = AccessLevel.PROTECTED)
public class InMemoryCache<K, V> extends Cache<K, V> {

    private final Long timeToLive;

    protected InMemoryCache(Long timeToLiveSeconds, Long timerIntervalSeconds, Integer maxItems) {
        super(maxItems);
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

    public void cleanup() {
        cleanup(null);
    }

    public void cleanup(Predicate<K> keyFilter) {
        synchronized (getCacheMap()) {
            Iterator<Entry<K, CacheObject<V>>> itr = getCacheMap().entrySet().iterator();
            while (itr.hasNext()) {
                Entry<K, CacheObject<V>> entry = itr.next();
                if ((keyFilter == null || keyFilter.test(entry.getKey())) && entry.getValue().isExpired(timeToLive)) {
                    itr.remove();
                }
            }
            Thread.yield();
        }
    }
}
