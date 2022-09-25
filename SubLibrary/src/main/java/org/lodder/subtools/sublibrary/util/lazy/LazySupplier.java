package org.lodder.subtools.sublibrary.util.lazy;

import org.lodder.subtools.sublibrary.util.Nothing;

import com.pivovarit.function.ThrowingSupplier;

public class LazySupplier<T> extends LazyThrowingSupplier<T, Nothing> {

	public LazySupplier(ThrowingSupplier<T, Nothing> supplier) {
		super(supplier);
	}

	public LazySupplier(T value) {
		super(value);
	}
}
