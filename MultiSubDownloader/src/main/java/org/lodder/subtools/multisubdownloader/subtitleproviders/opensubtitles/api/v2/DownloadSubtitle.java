package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2;

import org.opensubtitles.api.DownloadApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.invoker.ApiException;
import org.opensubtitles.model.Download200Response;
import org.opensubtitles.model.DownloadRequest;

public class DownloadSubtitle extends OpenSubtitlesExecuter {
    private final ApiClient apiClient;

    private int fileId;

    public DownloadSubtitle(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public DownloadSubtitle fileId(int fileId) {
        this.fileId = fileId;
        return this;
    }

    public Download200Response download() throws ApiException {
        return execute(() -> new DownloadApi(apiClient).download(new DownloadRequest().fileId(fileId)));
    }
}
