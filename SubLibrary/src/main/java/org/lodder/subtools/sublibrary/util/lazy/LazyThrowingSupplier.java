package org.lodder.subtools.sublibrary.util.lazy;

import com.pivovarit.function.ThrowingConsumer;
import com.pivovarit.function.ThrowingSupplier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LazyThrowingSupplier<T, X extends Exception> {

	private final ThrowingSupplier<T, X> supplier;

	private T object;

	private final Object lock = new Object();

	private boolean initialized = false;

	public LazyThrowingSupplier(T value) {
		supplier = null;
		object = value;
		initialized = true;
	}

	public T get() throws X {
		if (!initialized) {
			synchronized (lock) {
				if (!initialized) {
					object = supplier.get();
					initialized = true;
				}
			}
		}
		return object;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void doIfInitialized(ThrowingConsumer<T, X> consumer) throws X {
		if (initialized) {
			consumer.accept(object);
		}
	}

	public void reset() {
		initialized = false;
	}
}
