package org.lodder.subtools.sublibrary.util.lazy;

import com.pivovarit.function.ThrowingBiFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LazyThrowingBiFunction<T, U, R, X extends Exception> {

    private final Object lock = new Object();

    private final ThrowingBiFunction<T, U, R, X> function;

    private R object;

    private volatile boolean initialized = false;

    public R apply(T arg1, U arg2) throws X {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    object = function.apply(arg1, arg2);
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
