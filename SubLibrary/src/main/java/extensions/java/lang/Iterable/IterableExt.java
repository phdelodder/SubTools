package extensions.java.lang.Iterable;

import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class IterableExt {

    public static <T> Stream<T> stream(@This Iterable<T> iterable) {
        return  StreamSupport.stream(iterable.spliterator(), false);
    }

    public static int size(@This Iterable<?> iterable) {
        return iterable instanceof Collection<?> collection ? collection.size() : (int) iterable.stream().count();
    }
}