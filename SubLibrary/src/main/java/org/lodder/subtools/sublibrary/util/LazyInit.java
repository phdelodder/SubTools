package org.lodder.subtools.sublibrary.util;

import com.pivovarit.function.ThrowingSupplier;

public class LazyInit<T> extends LazyInitThrow<T, Nothing> {

	public LazyInit(ThrowingSupplier<T, Nothing> supplier) {
		super(supplier);
	}

	public LazyInit(T value) {
		super(value);
	}
}
