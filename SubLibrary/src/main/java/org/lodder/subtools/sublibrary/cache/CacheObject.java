package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.function.Function;

import manifold.ext.props.rt.api.val;
import manifold.science.measures.Time;

public sealed interface CacheObject<T> extends Serializable permits ExpiringCacheObject, TemporaryCacheObject {

    @val Time created;
    @val T value;
    @val Time age;

    void updateLastAccessed();

    boolean isExpired(Time ttl);

    String toString(Function<T, String> valueToStringMapper);

    static <T> CacheObject<T> fromString(String string, Function<String, T> valueToObjectMapper) {
        return ExpiringCacheObject.fromString(string, valueToObjectMapper)
            .orElseGet(() -> TemporaryCacheObject.fromString(string, valueToObjectMapper)
                .orElseThrow(() -> new IllegalStateException("Could not parse value: $string")));
    }
}
