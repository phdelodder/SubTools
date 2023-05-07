package org.lodder.subtools.sublibrary.util;

import java.util.Collection;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import name.falgout.jeffrey.throwing.stream.ThrowingStream;
import net.jodah.typetools.TypeResolver;

@UtilityClass
public class StreamExtension {

    public static <T, E extends Exception> ThrowingStream<T, E> asThrowingStream(Stream<T> stream, Class<E> exceptionType) {
        return ThrowingStream.of(stream, exceptionType);
    }

    public static <C extends Collection<T>, T, X extends Exception> ThrowingStream<T, X> normalFlatMap(ThrowingStream<C, X> stream) {
        @SuppressWarnings("unchecked")
        Class<X> exceptionType = (Class<X>) TypeResolver.resolveRawArguments(ThrowingStream.class, stream.getClass())[1];
        return stream.normalFlatMap(collection -> ThrowingStream.of(collection.stream(), exceptionType));
    }
}
