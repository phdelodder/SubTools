package org.lodder.subtools.sublibrary.util.lazy;

import com.pivovarit.function.ThrowingSupplier;
import org.lodder.subtools.sublibrary.util.Nothing;

public class LazySupplier<T> extends LazyThrowingSupplier<T, Nothing> {

    public LazySupplier(ThrowingSupplier<T, Nothing> supplier) {
        super(supplier);
    }

    public LazySupplier(T value) {
        super(value);
    }
}
