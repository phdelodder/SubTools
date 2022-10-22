package org.lodder.subtools.sublibrary.util;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;

import org.lodder.subtools.sublibrary.util.throwingfunction.ThrowingIntConsumer;

import com.pivovarit.function.ThrowingConsumer;
import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.ThrowingIntFunction;
import com.pivovarit.function.ThrowingRunnable;
import com.pivovarit.function.ThrowingSupplier;

import lombok.experimental.UtilityClass;
import name.falgout.jeffrey.throwing.ThrowingIntPredicate;
import name.falgout.jeffrey.throwing.ThrowingLongConsumer;
import name.falgout.jeffrey.throwing.ThrowingLongFunction;
import name.falgout.jeffrey.throwing.ThrowingToIntFunction;

@UtilityClass
public class OptionalExtension {

    public static <T, X extends Exception> Optional<T> ifPresentDo(Optional<T> optional, ThrowingConsumer<T, X> consumer) throws X {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        }
        return optional;
    }

    public static <X extends Exception> OptionalInt ifPresentDo(OptionalInt optional, ThrowingIntConsumer<X> consumer) throws X {
        if (optional.isPresent()) {
            consumer.accept(optional.getAsInt());
        }
        return optional;
    }

    public static <X extends Exception> OptionalLong ifPresentDo(OptionalLong optional, ThrowingLongConsumer<X> consumer) throws X {
        if (optional.isPresent()) {
            consumer.accept(optional.getAsLong());
        }
        return optional;
    }

    ////

    public static <T, X extends Exception> void orElseDo(Optional<T> optional, ThrowingRunnable<X> consumer) throws X {
        if (optional.isEmpty()) {
            consumer.run();
        }
    }

    public static <X extends Exception> void orElseDo(OptionalInt optional, ThrowingRunnable<X> consumer) throws X {
        if (optional.isEmpty()) {
            consumer.run();
        }
    }

    public static <X extends Exception> void orElseDo(OptionalLong optional, ThrowingRunnable<X> consumer) throws X {
        if (optional.isEmpty()) {
            consumer.run();
        }
    }

    //

    public static <T, X extends Exception> Optional<T> ifEmptyDo(Optional<T> optional, ThrowingRunnable<X> runnable) throws X {
        if (optional.isEmpty()) {
            runnable.run();
        }
        return optional;
    }

    //

    public static <T, X extends Exception> Optional<T> orElseMap(Optional<T> optional, ThrowingSupplier<Optional<T>, X> supplier) throws X {
        return optional.isPresent() ? optional : supplier.get();
    }

    public static <X extends Exception> OptionalInt orElseMap(OptionalInt optionalInt, ThrowingSupplier<OptionalInt, X> intSupplier) throws X {
        return optionalInt.isPresent() ? optionalInt : intSupplier.get();
    }

    //

    public static <T, S, X extends Exception> T mapOrElseGet(Optional<S> optional, ThrowingFunction<S, T, X> ifPresentFunction,
            ThrowingSupplier<T, X> absentSupplier) throws X {
        return optional.isPresent() ? ifPresentFunction.apply(optional.get()) : absentSupplier.get();
    }

    public static <T, X extends Exception> T mapOrElseGet(OptionalInt optionalInt, ThrowingIntFunction<T, X> ifPresentFunction,
            ThrowingSupplier<T, X> absentSupplier) throws X {
        return optionalInt.isPresent() ? ifPresentFunction.apply(optionalInt.getAsInt()) : absentSupplier.get();
    }

    //

    public static <T, X extends Exception> OptionalInt mapToInt(Optional<T> optional, ThrowingToIntFunction<T, X> mapper) throws X {
        return optional.isPresent() ? OptionalInt.of(mapper.applyAsInt(optional.get())) : OptionalInt.empty();
    }

    public static <T> OptionalInt mapToOptionalInt(Optional<Integer> optional) {
        return optional.isPresent() ? OptionalInt.of(optional.get()) : OptionalInt.empty();
    }

    //

    public static <S, T, X extends Exception> Optional<T> mapToObj(Optional<S> optional, ThrowingFunction<S, T, X> mapper) throws X {
        return optional.isPresent() ? Optional.ofNullable(mapper.apply(optional.get())) : Optional.empty();
    }

    public static <T, X extends Exception> Optional<T> mapToObj(OptionalInt optionalInt, ThrowingIntFunction<T, X> mapper) throws X {
        return optionalInt.isPresent() ? Optional.ofNullable(mapper.apply(optionalInt.getAsInt())) : Optional.empty();
    }

    public static <T, X extends Exception> Optional<T> mapToObj(OptionalLong optionalLong, ThrowingLongFunction<T, X> mapper) throws X {
        return optionalLong.isPresent() ? Optional.ofNullable(mapper.apply(optionalLong.getAsLong())) : Optional.empty();
    }

    //

    public static <T, X extends Exception> OptionalLong map(OptionalLong optionalLong, ThrowingLongFunction<Long, X> mapper) throws X {
        return optionalLong.isPresent() ? OptionalLong.of(mapper.apply(optionalLong.getAsLong())) : optionalLong;
    }

    //

    public static <T, S, X extends Exception> Optional<S> mapToOptionalObj(Optional<T> optional, ThrowingFunction<T, Optional<S>, X> mapper)
            throws X {
        return optional.isPresent() ? mapper.apply(optional.get()) : Optional.empty();
    }

    public static <T, X extends Exception> Optional<T> mapToOptionalObj(OptionalInt optionalInt, ThrowingIntFunction<Optional<T>, X> mapper)
            throws X {
        return optionalInt.isPresent() ? mapper.apply(optionalInt.getAsInt()) : Optional.empty();
    }

    //

    public static <T, X extends Exception> OptionalInt filter(OptionalInt optionalInt, ThrowingIntPredicate<X> predicate)
            throws X {
        return optionalInt.isPresent() && predicate.test(optionalInt.getAsInt()) ? optionalInt : OptionalInt.empty();
    }

    //

    public static <T, X extends Exception> void ifPresentOrThrow(Optional<T> optional, ThrowingConsumer<T, X> consumer,
            Supplier<X> exceptionSupplier) throws X {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        } else {
            throw exceptionSupplier.get();
        }
    }

    public static <X extends Exception> void ifPresentOrThrow(OptionalInt optionalInt, ThrowingIntConsumer<X> consumer,
            Supplier<X> exceptionSupplier) throws X {
        if (optionalInt.isPresent()) {
            consumer.accept(optionalInt.getAsInt());
        } else {
            throw exceptionSupplier.get();
        }
    }
}
