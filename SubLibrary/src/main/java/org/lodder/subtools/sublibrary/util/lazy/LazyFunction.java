package org.lodder.subtools.sublibrary.util.lazy;

import org.lodder.subtools.sublibrary.util.Nothing;

import com.pivovarit.function.ThrowingFunction;

public class LazyFunction<T, S> extends LazyThrowingFunction<T, S, Nothing> {

    public LazyFunction(ThrowingFunction<T, S, Nothing> function) {
        super(function);
	}

}
