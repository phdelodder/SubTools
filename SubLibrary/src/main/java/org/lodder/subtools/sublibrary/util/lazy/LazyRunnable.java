package org.lodder.subtools.sublibrary.util.lazy;

import org.lodder.subtools.sublibrary.util.Nothing;

import com.pivovarit.function.ThrowingRunnable;

public class LazyRunnable extends LazyThrowingRunnable<Nothing> {

    public LazyRunnable(ThrowingRunnable<Nothing> runnable) {
        super(runnable);
    }
}
