package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    public static <T, K, V> Collector<T, Map<K, V>, Map<K, V>> mapCollector(
            BiConsumer<Map<K, V>, T> accumulator) {
        return mapCollector(HashMap::new, accumulator);
    }

    public static <T, R extends Map<K, V>, K, V> Collector<T, R, R> mapCollector(Supplier<R> supplier,
            BiConsumer<R, T> accumulator) {
        return Collector.of(
                supplier,
                accumulator,
                (R m1, R m2) -> {
                    m1.putAll(m2);
                    return m1;
                });
    }

    public static <T, K> Collector<T, Set<K>, Set<K>> setCollector(BiConsumer<Set<K>, T> accumulator) {
        return setCollector(HashSet::new, accumulator);
    }

    public static <T, R extends Set<K>, K> Collector<T, R, R> setCollector(Supplier<R> supplier,
            BiConsumer<R, T> accumulator) {
        return Collector.of(
                supplier,
                accumulator,
                (R m1, R m2) -> {
                    m1.addAll(m2);
                    return m1;
                });
    }
}
