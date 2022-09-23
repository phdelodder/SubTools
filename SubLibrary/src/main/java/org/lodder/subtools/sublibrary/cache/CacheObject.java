package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
class CacheObject<T> implements Serializable {

    private static final long serialVersionUID = 3852086993086134232L;
    private static final Pattern PATTERN = Pattern.compile("created:(.*?)|lastAccessed:(.*?)|value:(.*)");
    private final long created;
    private long lastAccessed = System.currentTimeMillis();
    private T value;

    private CacheObject(long created, long lastAccessed, T value) {
        this.created = created;
        this.lastAccessed = lastAccessed;
        this.value = value;
    }


    protected CacheObject(T value) {
        this.created = System.currentTimeMillis();
        this.value = value;
    }

    public void updateLastAccessed() {
        lastAccessed = System.currentTimeMillis();
    }

    public String toString(Function<T, String> valueToStringMapper) {
        return "created:%s|lastAccessed:%s|value:%s".formatted(created, lastAccessed, valueToStringMapper.apply(value));
    }

    public static <T> CacheObject<T> fromString(String string, Function<String, T> valueToObjectMapper) {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            long created = Long.parseLong(matcher.group(1));
            long lastAccessed = Long.parseLong(matcher.group(2));
            String value = matcher.group(3);
            return new CacheObject<>(created, lastAccessed, valueToObjectMapper.apply(value));
        }
        throw new IllegalStateException("Coudl not parse value: " + string);
    }
}
