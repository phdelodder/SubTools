package org.lodder.subtools.sublibrary.util;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A type-specific {@link Consumer}; provides methods to consume a primitive type both as object and
 * as primitive.
 *
 * <p>
 * Except for the boolean case, this interface extends both a parameterized
 * {@link java.util.function.Consumer} and a type-specific JDK consumer (e.g.,
 * {@link java.util.function.IntConsumer}). For types missing a type-specific JDK consumer (e.g.,
 * {@code short} or {@code float}), we extend the consumer associated with the smallest primitive
 * type that can represent the current type (e.g., {@code int} or {@code double}, respectively).
 *
 * @see Consumer
 * source: it.unimi.dsi.fastutil
 */
@FunctionalInterface
public interface BooleanConsumer extends Consumer<Boolean> {
    /**
     * Performs this operation on the given input.
     *
     * @param t the input.
     */
    void accept(boolean t);

    /**
     * {@inheritDoc}
     *
     * @deprecated Please use the corresponding type-specific method instead.
     */
    @Deprecated
    @Override
    default void accept(final Boolean t) {
        this.accept(t.booleanValue());
    }

    /**
     * Returns a composed type-specific consumer that performs, in sequence, this operation followed by
     * the {@code after} operation.
     *
     * @param after the operation to perform after this operation.
     * @return a composed {@code Consumer} that performs in sequence this operation followed by the
     *         {@code after} operation.
     * @see Consumer#andThen
     * @apiNote Implementing classes should generally override this method and keep the default
     *          implementation of the other overloads, which will delegate to this method (after proper
     *          conversions).
     */
    default BooleanConsumer andThen(final BooleanConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Please use the corresponding type-specific method instead.
     */
    @Deprecated
    @Override
    default Consumer<Boolean> andThen(final Consumer<? super Boolean> after) {
        return Consumer.super.andThen(after);
    }
}
