package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class SerializableDiskCache<K extends Serializable, V extends Serializable> extends DiskCache<K, V> {

    public static final Object LOCK = new Object();

    @Getter(value = AccessLevel.PROTECTED)
    private final Class<K> dbKeyType;
    @Getter(value = AccessLevel.PROTECTED)
    private final Class<V> dbValueType;

    @SuppressWarnings("rawtypes")
    public static DiskCacheBuilderKeyTypeIntf cacheBuilder() {
        return new DiskCacheBuilder();
    }

    public interface DiskCacheBuilderKeyTypeIntf {
        <K extends Serializable> DiskCacheBuilderValueTypeIntf<K> keyType(Class<K> keyType);
    }

    public interface DiskCacheBuilderValueTypeIntf<K extends Serializable> {
        <V extends Serializable> DiskCacheBuilderOtherIntf<K, V> valueType(Class<V> valueType);
    }

    public interface DiskCacheBuilderOtherIntf<K extends Serializable, V extends Serializable> {
        DiskCacheBuilderOtherIntf<K, V> cacheName(String cacheName);

        DiskCacheBuilderOtherIntf<K, V> timeToLive(Long timeToLive);

        DiskCacheBuilderOtherIntf<K, V> timerInterval(Long timerInterval);

        DiskCacheBuilderOtherIntf<K, V> maxItems(Integer maxItems);

        DiskCacheBuilderPasswordIntf<K, V> username(String username);

        SerializableDiskCache<K, V> build();
    }

    public interface DiskCacheBuilderPasswordIntf<K extends Serializable, V extends Serializable> {
        DiskCacheBuilderOtherIntf<K, V> password(String password);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class DiskCacheBuilder<K extends Serializable, V extends Serializable>
            implements DiskCacheBuilderOtherIntf<K, V>, DiskCacheBuilderPasswordIntf<K, V>, DiskCacheBuilderValueTypeIntf<K>,
            DiskCacheBuilderKeyTypeIntf {
        private Class<K> keyType;
        private Class<V> valueType;
        private Long timeToLive;
        private Long timerInterval;
        private Integer maxItems;
        private String username;
        private String password;
        private String cacheName;

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Serializable> DiskCacheBuilder<T, V> keyType(Class<T> keyType) {
            this.keyType = (Class<K>) keyType;
            return (DiskCacheBuilder<T, V>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Serializable> DiskCacheBuilder<K, T> valueType(Class<T> valueType) {
            this.valueType = (Class<V>) valueType;
            return (DiskCacheBuilder<K, T>) this;
        }

        @Override
        public SerializableDiskCache<K, V> build() {
            return new SerializableDiskCache<>(keyType, valueType, timeToLive, timerInterval, maxItems, username, password, cacheName);
        }
    }

    private SerializableDiskCache(Class<K> keyType, Class<V> valueType, Long timeToLive, Long timerInterval, Integer maxItems, String username,
            String password, String cacheName) {
        super(timeToLive, timerInterval, maxItems, username, password, cacheName);
        this.dbKeyType = keyType;
        this.dbValueType = valueType;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected K diskObjectToKey(Object key) {
        return (K) key;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CacheObject<V> diskCacheObjectToValue(Object value) {
        return (CacheObject<V>) value;
    }

    @Override
    protected Object keyToDiskObject(K key) {
        return key;
    }

    @Override
    protected Object cacheObjectToDiskObject(CacheObject<V> value) {
        return value;
    }

}
