package org.lodder.subtools.sublibrary.cache;

import static manifold.science.util.UnitConstants.*;

import java.io.Serial;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.ToString;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import manifold.science.measures.Time;

@ToString
sealed class TemporaryCacheObject<T> implements CacheObject<T> permits TemporarySerializableCacheObject {

    @Serial
    private static final long serialVersionUID = -152474119228350222L;
    private static final Pattern PATTERN = Pattern.compile("created:(.*?)|expire:(.*?)|value:(.*)");
    @override @val Time created;
    @val Time timeToLive;
    @override @val T value;

    protected TemporaryCacheObject(Time timeToLive, T value) {
        this(Time.now(), timeToLive, value);
    }

    private TemporaryCacheObject(Time created, Time timeToLive, T value) {
        this.created = created;
        this.timeToLive = timeToLive;
        this.value = value;
    }

    @Override
    public boolean isExpired(Time ttl) {
        return isExpired();
    }

    public boolean isExpired() {
        return Time.now().isAfter(created + timeToLive);
    }


    @Override
    public void updateLastAccessed() {
        // do nothing
    }

    @Override
    public String toString(Function<T, String> valueToStringMapper) {
        return "created:%s|expire:%s|value:%s".formatted(created, timeToLive, valueToStringMapper.apply(value));
    }

    public static <T> Optional<TemporaryCacheObject<T>> fromString(String string,
            Function<String, T> valueToObjectMapper) {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            Time created = Time.create(Long.parseLong(matcher.group(1)), ms);
            Time timeToLive = Time.create(Long.parseLong(matcher.group(2)), ms);
            String value = matcher.group(3);
            return Optional.of(new TemporaryCacheObject<>(created, timeToLive, valueToObjectMapper.apply(value)));
        }
        return Optional.empty();
    }

    @Override
    public Time getAge() {
        return created;
    }
}
