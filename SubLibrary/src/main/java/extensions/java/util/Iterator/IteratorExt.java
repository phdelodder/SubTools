package extensions.java.util.Iterator;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class IteratorExt {

    public static <E> Stream<E> stream(@This Iterator<E> iterator) {
        return Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED).stream();
    }
}