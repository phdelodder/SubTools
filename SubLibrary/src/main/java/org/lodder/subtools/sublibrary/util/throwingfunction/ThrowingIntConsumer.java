package org.lodder.subtools.sublibrary.util.throwingfunction;

import static java.util.Objects.*;

import java.util.Objects;
import java.util.function.IntConsumer;

import com.pivovarit.function.ThrowingIntFunction;
import com.pivovarit.function.exception.WrappedException;

/**
 * Represents a function that accepts one argument and does not return any value;
 * Function might throw a checked exception instance.
 *
 * @param <E> the type of the thrown checked exception
 *
 */
@FunctionalInterface
public interface ThrowingIntConsumer<E extends Exception> {

    void accept(int i) throws E;

    static IntConsumer unchecked(ThrowingIntConsumer<?> consumer) {
        return requireNonNull(consumer).uncheck();
    }

    /**
     * Returns a new BiConsumer instance which rethrows the checked exception using the Sneaky Throws pattern
     * @return BiConsumer instance that rethrows the checked exception using the Sneaky Throws pattern
     */
    static IntConsumer sneaky(ThrowingIntConsumer<?> consumer) {
        Objects.requireNonNull(consumer);
        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception e) {
                SneakyThrowUtil.sneakyThrow(e);
            }
        };
    }

    /**
     * Chains given ThrowingIntConsumer instance
     *
     * @param after - consumer that is chained after this instance
     * @return chained Consumer instance
     */
    default ThrowingIntConsumer<E> andThenConsume(final ThrowingIntConsumer<? extends E> after) {
        requireNonNull(after);
        return i -> {
            accept(i);
            after.accept(i);
        };
    }

    /**
     * @return this consumer instance as a Function instance
     */
    default ThrowingIntFunction<Void, E>

    asFunction() {
        return arg -> {
            accept(arg);
            return null;
        };
    }

    /**
     * @return a Consumer instance which wraps thrown checked exception instance into a RuntimeException
     */
    default IntConsumer uncheck() {
        return i -> {
            try {
                accept(i);
            } catch (final Exception e) {
                throw new WrappedException(e);
            }
        };
    }
}
