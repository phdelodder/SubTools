package org.lodder.subtools.sublibrary.util.lazy;

import com.pivovarit.function.ThrowingBiFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LazyThrowingBiFunction<T, S, V, X extends Exception> {

    private final ThrowingBiFunction<T, S, V, X> function;

    private V object;

	private final Object lock = new Object();

	private boolean initialized = false;

    public V apply(T arg1, S arg2) throws X {
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
