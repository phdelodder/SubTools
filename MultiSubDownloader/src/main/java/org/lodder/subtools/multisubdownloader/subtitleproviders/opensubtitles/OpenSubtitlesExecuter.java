package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles;

import org.opensubtitles.invoker.ApiException;

import com.pivovarit.function.ThrowingSupplier;

public abstract class OpenSubtitlesExecuter {

    protected <T> T execute(ThrowingSupplier<T, ApiException> callable) throws ApiException {
        try {
            return callable.get();
        } catch (ApiException e) {
            if (e.getCode() == 429) {
                // Too Many Requests
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
                // retry
                return callable.get();
            } else {
                throw e;
            }
        }
    }
}
