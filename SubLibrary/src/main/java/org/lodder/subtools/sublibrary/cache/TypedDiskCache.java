package org.lodder.subtools.sublibrary.cache;

import java.util.function.Function;

import lombok.Setter;
import lombok.experimental.Accessors;

public class TypedDiskCache<K, V> extends DiskCache<K, V> {

    private final Function<K, String> toStringMapperKey;
    private final Function<String, K> toObjectMapperKey;
    private final Function<V, String> toStringMapperValue;
    private final Function<String, V> toObjectMapperValue;

    @SuppressWarnings("rawtypes")
    public static DiskCacheBuilderToStringMapperKeyIntf cacheBuilder() {
        return new DiskCacheBuilder();
    }

    public interface DiskCacheBuilderToStringMapperKeyIntf {
        <K> DiskCacheBuilderToObjectMapperKeyIntf<K> toStringMapperKey(Function<K, String> toStringMapperKey);
    }

    public interface DiskCacheBuilderToObjectMapperKeyIntf<K> {
        DiskCacheBuilderToStringMapperValueIntf<K> toObjectMapperKey(Function<String, K> toObjectMapperKey);
    }

    public interface DiskCacheBuilderToStringMapperValueIntf<K> {
        <V> DiskCacheBuilderToObjectMapperValueIntf<K, V> toStringMapperValue(Function<V, String> toStringMapperValue);
    }

    public interface DiskCacheBuilderToObjectMapperValueIntf<K, V> {
        DiskCacheBuilderOtherIntf<K, V> toObjectMapperValue(Function<String, V> toObjectMapperValue);
    }

    public interface DiskCacheBuilderOtherIntf<K, V> {
        DiskCacheBuilderOtherIntf<K, V> cacheName(String cacheName);

        DiskCacheBuilderOtherIntf<K, V> timeToLive(Long timeToLive);

        DiskCacheBuilderOtherIntf<K, V> timerInterval(Long timerInterval);

        DiskCacheBuilderOtherIntf<K, V> maxItems(Integer maxItems);

        DiskCacheBuilderPasswordIntf<K, V> username(String username);

        TypedDiskCache<K, V> build();
    }

    public interface DiskCacheBuilderPasswordIntf<K, V> {
        DiskCacheBuilderOtherIntf<K, V> password(String password);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class DiskCacheBuilder<K, V> implements DiskCacheBuilderPasswordIntf<K, V>, DiskCacheBuilderOtherIntf<K, V>,
            DiskCacheBuilderToObjectMapperValueIntf<K, V>, DiskCacheBuilderToStringMapperValueIntf<K>, DiskCacheBuilderToObjectMapperKeyIntf<K>,
            DiskCacheBuilderToStringMapperKeyIntf {
        private Long timeToLive;
        private Long timerInterval;
        private Integer maxItems;
        private String username;
        private String password;
        private String cacheName;
        private Function<K, String> toStringMapperKey;
        private Function<String, K> toObjectMapperKey;
        private Function<V, String> toStringMapperValue;
        private Function<String, V> toObjectMapperValue;

        @Override
        @SuppressWarnings("unchecked")
        public <T> DiskCacheBuilder<T, V> toStringMapperKey(Function<T, String> toStringMapperKey) {
            this.toStringMapperKey = (Function<K, String>) toStringMapperKey;
            return (DiskCacheBuilder<T, V>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> DiskCacheBuilder<K, T> toStringMapperValue(Function<T, String> toStringMapperValue) {
            this.toStringMapperValue = (Function<V, String>) toStringMapperValue;
            return (DiskCacheBuilder<K, T>) this;
        }

        @Override
        public TypedDiskCache<K, V> build() {
            return new TypedDiskCache<>(timeToLive, timerInterval, maxItems, username, password, toStringMapperKey, toObjectMapperKey,
                    toStringMapperValue, toObjectMapperValue, cacheName);
        }
    }

    private TypedDiskCache(Long timeToLive, Long timerInterval, Integer maxItems, String username, String password,
            Function<K, String> toStringMapperKey,
            Function<String, K> toObjectMapperKey, Function<V, String> toStringMapperValue, Function<String, V> toObjectMapperValue,
            String cacheName) {
        super(timeToLive, timerInterval, maxItems, username, password, cacheName);
        this.toStringMapperKey = toStringMapperKey;
        this.toObjectMapperKey = toObjectMapperKey;
        this.toStringMapperValue = toStringMapperValue;
        this.toObjectMapperValue = toObjectMapperValue;
    }

    @Override
    protected K diskObjectToKey(Object key) {
        return toObjectMapperKey.apply((String) key);
    }

    @Override
    protected CacheObject<V> diskCacheObjectToValue(Object value) {
        return CacheObject.fromString((String) value, toObjectMapperValue);
    }

    @Override
    protected Object keyToDiskObject(K key) {
        return toStringMapperKey.apply(key);
    }

    @Override
    protected Object cacheObjectToDiskObject(CacheObject<V> value) {
        return value.toString(toStringMapperValue);
    }

    @Override
    protected Class getDbKeyType() {
        return String.class;
    }

    @Override
    protected Class getDbValueType() {
        return String.class;
    }

}
