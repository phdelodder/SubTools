package org.lodder.subtools.sublibrary.cache;

import java.util.Optional;
import java.util.function.Function;

public interface CacheObject<T> {

    long getCreated();

    T getValue();

    void updateLastAccessed();

    boolean isExpired(long ttl);

    String toString(Function<T, String> valueToStringMapper);

    static <T> CacheObject<T> fromString(String string, Function<String, T> valueToObjectMapper) {
        Optional<CacheObject<T>> cacheObject = ExpiringCacheObject.fromString(string, valueToObjectMapper);
        if (cacheObject.isPresent()) {
            return cacheObject.get();
        }
        Optional<TemporaryCacheObject<T>> temporaryCacheObject = TemporaryCacheObject.fromString(string, valueToObjectMapper);
        if (temporaryCacheObject.isPresent()) {
            return temporaryCacheObject.get();
        }
        throw new IllegalStateException("Could not parse value: " + string);
    }
}
