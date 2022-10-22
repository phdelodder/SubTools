package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class TemporaryCacheObject<T> implements CacheObject<T>, Serializable {

    private static final long serialVersionUID = -152474119228350222L;
    private static final Pattern PATTERN = Pattern.compile("created:(.*?)|expire:(.*?)|value:(.*)");
    private final long created;
    private final long timeToLive;
    private T value;

    protected TemporaryCacheObject(long timeToLive, T value) {
        this.created = System.currentTimeMillis();
        this.timeToLive = timeToLive;
        this.value = value;
    }

    @Override
    public boolean isExpired(long ttl) {
        return isExpired();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > (created + timeToLive);
    }


    @Override
    public void updateLastAccessed() {
        // do nothing
    }

    @Override
    public String toString(Function<T, String> valueToStringMapper) {
        return "created:%s|expire:(.*?)|value:%s".formatted(created, timeToLive, valueToStringMapper.apply(value));
    }

    public static <T> Optional<TemporaryCacheObject<T>> fromString(String string, Function<String, T> valueToObjectMapper) {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            long created = Long.parseLong(matcher.group(1));
            long timeToLive = Long.parseLong(matcher.group(2));
            String value = matcher.group(3);
            return Optional.of(new TemporaryCacheObject<>(created, timeToLive, valueToObjectMapper.apply(value)));
        }
        return Optional.empty();
    }
}
