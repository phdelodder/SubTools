package extensions.java.util.Optional;

import java.util.Optional;
import java.util.OptionalInt;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;
import name.falgout.jeffrey.throwing.ThrowingConsumer;
import name.falgout.jeffrey.throwing.ThrowingFunction;
import name.falgout.jeffrey.throwing.ThrowingSupplier;
import name.falgout.jeffrey.throwing.ThrowingToIntFunction;

@UtilityClass
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Extension
public class OptionalExt {

    /**
     * If the value is present, apply the {@link ThrowingFunction} and return the value. Otherwise return {@code null}
     *
     * @param optional input object of the extension method
     * @param function the {@link ThrowingFunction} to apply to the value if present
     * @param <T> The type of value of the input {@code Optional}
     * @param <S> type of the return value
     * @param <X> type of the exception that can be thrown
     * @return the result of the {@link ThrowingFunction} if the value is present, otherwise {@code null}
     * @throws X exception type of the throwing Function
     */
    public static <T, S, X extends Throwable> Optional<S> mapThrowing(@This Optional<T> optional,
            ThrowingFunction<T, S, X> function) throws X {
        return optional.isPresent() ? Optional.ofNullable(function.apply(optional.get())) : Optional.empty();
    }

    public static <T, X extends Exception> OptionalInt mapToInt(@This Optional<T> optional,
            ThrowingToIntFunction<T, X> mapper) throws X {
        return optional.isPresent() ? OptionalInt.of(mapper.applyAsInt(optional.get())) : OptionalInt.empty();
    }

    public static <T, X extends Exception> @Self Optional<T> useIfPresent(@This Optional<T> optional,
            ThrowingConsumer<T, X> consumer) throws X {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        }
        return optional;
    }

    public static <T, X extends Throwable> T orElseGetThrowing(@This Optional<T> optional,
        ThrowingSupplier<T, X> supplier) throws X {
        return optional.isPresent() ? optional.get() : supplier.get();
    }

}
