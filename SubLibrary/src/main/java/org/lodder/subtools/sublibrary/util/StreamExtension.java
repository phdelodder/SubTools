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

    // public static <R, T, X extends Exception> ThrowingStream<R, X> normalStreamFlatMap(ThrowingStream<T, X> stream,
    // Function<? super T, ? extends Stream<? extends R>> mapper, Class<X> exceptionType) {
    //
    // // T t = stream.findAny().get();
    // // Stream<? extends R> apply = mapper.apply(t);
    // // ThrowingStream<? extends R, ? extends X> of = ThrowingStream.of(apply, exceptionType);
    // // ThrowingStream<? extends R, ? extends X> of2 = ThrowingStream.of((Stream<? extends R>) mapper.apply(t), exceptionType);
    // //
    // // Function<? super T, ? extends ThrowingStream<? extends R, ? extends X>> throwingMapper =
    // // e -> ThrowingStream.of(mapper.apply(e), exceptionType);
    // Reflect.on(stream).call("getExceptionClass");
    // return stream.normalFlatMap(e -> ThrowingStream.of((Stream<? extends R>) mapper.apply(e), exceptionType));
    //
    // // stream.flatMap(null)
    // // return flatMap(mapper::apply);
    //
    // // @SuppressWarnings("unchecked")
    // // Function<? super ThrowingStream<? extends R, ? extends X>, ? extends Stream<? extends R>> c =
    // // s -> ThrowingBridge.of((ThrowingStream<? extends R, X>) s, getExceptionClass());
    // // return newStream(getDelegate().flatMap(getExceptionMasker().mask(mapper.andThen(c::apply))));
    // }

    public static <C extends Collection<T>, T, X extends Exception> ThrowingStream<T, X> normalFlatMap(ThrowingStream<C, X> stream) {

        // T t = stream.findAny().get();
        // Stream<? extends R> apply = mapper.apply(t);
        // ThrowingStream<? extends R, ? extends X> of = ThrowingStream.of(apply, exceptionType);
        // ThrowingStream<? extends R, ? extends X> of2 = ThrowingStream.of((Stream<? extends R>) mapper.apply(t), exceptionType);
        //
        // Function<? super T, ? extends ThrowingStream<? extends R, ? extends X>> throwingMapper =
        // e -> ThrowingStream.of(mapper.apply(e), exceptionType);
        Class<X> exceptionType = (Class<X>) TypeResolver.resolveRawArguments(ThrowingStream.class, stream.getClass())[1];
        return stream.normalFlatMap(collection -> ThrowingStream.of(collection.stream(), exceptionType));

        // Reflect.on(stream).call("getExceptionClass");
        // return stream.normalFlatMap(e -> ThrowingStream.of((Stream<? extends R>) mapper.apply(e), exceptionType));

        // stream.flatMap(null)
        // return flatMap(mapper::apply);

        // @SuppressWarnings("unchecked")
        // Function<? super ThrowingStream<? extends R, ? extends X>, ? extends Stream<? extends R>> c =
        // s -> ThrowingBridge.of((ThrowingStream<? extends R, X>) s, getExceptionClass());
        // return newStream(getDelegate().flatMap(getExceptionMasker().mask(mapper.andThen(c::apply))));
    }

}
