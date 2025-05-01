package org.lodder.subtools.sublibrary.util.lazy;

import com.pivovarit.function.ThrowingRunnable;
import org.lodder.subtools.sublibrary.util.Nothing;

public class LazyRunnable extends LazyThrowingRunnable<Nothing> {

    public LazyRunnable(ThrowingRunnable<Nothing> runnable) {
        super(runnable);
    }
}
