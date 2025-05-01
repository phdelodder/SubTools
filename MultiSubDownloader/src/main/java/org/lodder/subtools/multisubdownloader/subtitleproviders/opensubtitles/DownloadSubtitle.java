package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.exception.OpenSubtitlesException;
import org.opensubtitles.api.DownloadApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.model.Download200Response;
import org.opensubtitles.model.DownloadRequest;

@RequiredArgsConstructor
public final class DownloadSubtitle extends OpenSubtitlesExecuter {
    private final ApiClient apiClient;

    private int fileId;

    public Download200Response download() throws OpenSubtitlesException {
        try {
            return execute(() -> new DownloadApi(apiClient)
                .download("SubTools", new DownloadRequest().fileId(fileId)));
        } catch (Exception e) {
            throw new OpenSubtitlesException(e);
        }
    }

    public DownloadSubtitle fileId(int fileId) {
        this.fileId = fileId;
        return this;
    }
}
