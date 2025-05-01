package org.lodder.subtools.sublibrary.util.lazy;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.sublibrary.util.throwingfunction.ThrowingQuadFunction;

@RequiredArgsConstructor
public class LazyThrowingQuadFunction<T, U, V, W, R, X extends Exception> {

    private final Object lock = new Object();

    private final ThrowingQuadFunction<T, U, V, W, R, X> function;

    private R object;

    private volatile boolean initialized = false;

    public R apply(T arg1, U arg2, V arg3, W arg4) throws X {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    object = function.apply(arg1, arg2, arg3, arg4);
                    initialized = true;
                }
            }
        }
        return object;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void reset() {
        initialized = false;
    }
}
