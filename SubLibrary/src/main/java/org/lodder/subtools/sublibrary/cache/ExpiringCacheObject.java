package org.lodder.subtools.sublibrary.cache;

import static manifold.ext.props.rt.api.PropOption.*;
import static manifold.science.util.UnitConstants.*;

import java.io.Serial;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.set;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import manifold.science.measures.Time;
 
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
sealed class ExpiringCacheObject<T> implements CacheObject<T> permits ExpiringSerializableCacheObject {

    @Serial
    private static final long serialVersionUID = 3852086993086134232L;
    private static final Pattern PATTERN = Pattern.compile("created:(.*?)|lastAccessed:(.*?)|value:(.*)");

    @override @val Time created;
    @var @set(Private) Time lastAccessed = Time.now();
    @override @var T value;

    protected ExpiringCacheObject(T value) {
        this.created = Time.now();
        this.value = value;
    }

    @Override
    public void updateLastAccessed() {
        lastAccessed = Time.now();
    }

    @Override
    public boolean isExpired(Time ttl) {
        return Time.now().isAfter(lastAccessed + ttl);
    }

    @Override
    public String toString(Function<T, String> valueToStringMapper) {
        return "created:$created|lastAccessed:$lastAccessed|value:${valueToStringMapper.apply(value)}";
    }

    public static <T> Optional<CacheObject<T>> fromString(String string, Function<String, T> valueToObjectMapper) {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            Time created = Time.create(Long.parseLong(matcher.group(1)), ms);
            Time lastAccessed = Time.create(Long.parseLong(matcher.group(2)), ms);
            String value = matcher.group(3);
            return Optional.of(new ExpiringCacheObject<>(created, lastAccessed, valueToObjectMapper.apply(value)));
        }
        return Optional.empty();
    }

    @Override
    public Time getAge() {
        return lastAccessed;
    }
}
