package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.exception.OpenSubtitlesException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.opensubtitles.api.AuthenticationApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.invoker.ApiException;
import org.opensubtitles.model.Login200Response;
import org.opensubtitles.model.LoginRequest;

public class OpenSubtitlesApi implements SubtitleApi {

    private final String apikey = "lNNp0yv0ah8gytkmYPbHwuaATJqr4rS9";

    private final ApiClient apiClient;

    public OpenSubtitlesApi() {
        apiClient = new ApiClient();
        apiClient.setApiKey(apikey);
    }

    public OpenSubtitlesApi(String userName, String password) throws OpenSubtitlesException {
        this();
        login(userName, password);
    }

    public void login(String userName, String password) throws OpenSubtitlesException {
        try {
            Login200Response loginResponse =
                    new AuthenticationApi(apiClient).login("application/json", new LoginRequest().username(userName).password(password));
            apiClient.setBearerToken(loginResponse.getToken());
        } catch (ApiException e) {
            throw new OpenSubtitlesException(e);
        }
    }

    public SearchSubtitles searchSubtitles() {
        return new SearchSubtitles(apiClient);
    }

    public DownloadSubtitle downloadSubtitle() {
        return new DownloadSubtitle(apiClient);
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.OPENSUBTITLES;
    }
}
