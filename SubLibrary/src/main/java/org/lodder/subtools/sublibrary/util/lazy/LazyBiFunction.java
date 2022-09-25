package org.lodder.subtools.sublibrary.util.lazy;

import org.lodder.subtools.sublibrary.util.Nothing;

import com.pivovarit.function.ThrowingBiFunction;

public class LazyBiFunction<T, S, V> extends LazyThrowingBiFunction<T, S, V, Nothing> {

    public LazyBiFunction(ThrowingBiFunction<T, S, V, Nothing> function) {
        super(function);
    }

}
