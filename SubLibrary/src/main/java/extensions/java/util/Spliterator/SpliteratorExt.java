package extensions.java.util.Spliterator;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class SpliteratorExt {
    public static <T> Stream<T> stream(@This Spliterator<T> spliterator) {
        return StreamSupport.stream(spliterator, false);
    }
}