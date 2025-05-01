package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles;

import static manifold.science.measures.TimeUnit.*;
import static org.lodder.subtools.sublibrary.util.Sleep.*;

import com.pivovarit.function.ThrowingSupplier;
import org.opensubtitles.invoker.ApiException;

public abstract sealed class OpenSubtitlesExecuter permits DownloadSubtitle, SearchSubtitles {

    protected <T> T execute(ThrowingSupplier<T, ApiException> callable) throws ApiException {
        try {
            return callable.get();
        } catch (ApiException e) {
            if (e.getCode() == 429 || e.getMessage().contains("ratelimit")) {
                // Too Many Requests
                sleep(1 Second);
                // retry
                return callable.get();
            } else {
                throw e;
            }
        }
    }
}
