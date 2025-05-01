package org.lodder.subtools.sublibrary.util.throwingfunction;

import java.util.Objects;
import java.util.Optional;

import com.pivovarit.function.exception.WrappedException;
import org.lodder.subtools.sublibrary.util.function.QuadFunction;

@FunctionalInterface
public interface ThrowingQuadFunction<T, U, V, W, R, E extends Exception> {
    R apply(T var1, U var2, V var3, W var4) throws E;

    static <T, U, V, W, R> QuadFunction<T, U, V, W, R> unchecked(ThrowingQuadFunction<T, U, V, W, R, ?> function) {
        return ((ThrowingQuadFunction) Objects.requireNonNull(function)).unchecked();
    }

    static <T, U, V, W, R> QuadFunction<T, U, V, W, R> sneaky(ThrowingQuadFunction<? super T, ? super U, ? super V, ?
        super W, ? extends R, ?> function) {
        Objects.requireNonNull(function);
        return (t1, t2, t3, t4) -> {
            try {
                return function.apply(t1, t2, t3, t4);
            } catch (Exception ex) {
                return SneakyThrowUtil.sneakyThrow(ex);
            }
        };
    }

    static <T, U, V, W, R> QuadFunction<T, U, V, W, Optional<R>> lifted(ThrowingQuadFunction<T, U, V, W, R, ?> f) {
        return ((ThrowingQuadFunction) Objects.requireNonNull(f)).lift();
    }

    default QuadFunction<T, U, V, W, R> unchecked() {
        return (arg1, arg2, arg3, arg4) -> {
            try {
                return this.apply(arg1, arg2, arg3, arg4);
            } catch (Exception e) {
                throw new WrappedException(e);
            }
        };
    }

    default QuadFunction<T, U, V, W, Optional<R>> lift() {
        return (arg1, arg2, arg3, arg4) -> {
            try {
                return Optional.ofNullable(this.apply(arg1, arg2, arg3, arg4));
            } catch (Exception var4) {
                return Optional.empty();
            }
        };
    }
}
