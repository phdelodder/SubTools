package org.lodder.subtools.sublibrary.util.lazy;

import com.pivovarit.function.ThrowingRunnable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LazyThrowingRunnable<X extends Exception> {

	private final ThrowingRunnable<X> runnable;

	private final Object lock = new Object();

	private volatile boolean initialized = false;

    public void run() throws X {
		if (!initialized) {
			synchronized (lock) {
				if (!initialized) {
                    runnable.run();
					initialized = true;
				}
			}
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void reset() {
		initialized = false;
	}
}
