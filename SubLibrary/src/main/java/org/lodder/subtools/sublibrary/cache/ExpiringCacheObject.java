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
class ExpiringCacheObject<T> implements CacheObject<T>, Serializable {

    private static final long serialVersionUID = 3852086993086134232L;
    private static final Pattern PATTERN = Pattern.compile("created:(.*?)|lastAccessed:(.*?)|value:(.*)");
    private final long created;
    private long lastAccessed = System.currentTimeMillis();
    private T value;

    protected ExpiringCacheObject(T value) {
        this.created = System.currentTimeMillis();
        this.value = value;
    }

    @Override
    public void updateLastAccessed() {
        lastAccessed = System.currentTimeMillis();
    }

    @Override
    public boolean isExpired(long ttl) {
        return System.currentTimeMillis() > (lastAccessed + ttl);
    }

    @Override
    public String toString(Function<T, String> valueToStringMapper) {
        return "created:%s|lastAccessed:%s|value:%s".formatted(created, lastAccessed, valueToStringMapper.apply(value));
    }

    public static <T> Optional<CacheObject<T>> fromString(String string, Function<String, T> valueToObjectMapper) {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            long created = Long.parseLong(matcher.group(1));
            long lastAccessed = Long.parseLong(matcher.group(2));
            String value = matcher.group(3);
            return Optional.of(new ExpiringCacheObject<>(created, lastAccessed, valueToObjectMapper.apply(value)));
        }
        return Optional.empty();
    }
}
