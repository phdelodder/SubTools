package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.exception.OpenSubtitlesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpensubtitleSerieId;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.opensubtitles.api.AuthenticationApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.invoker.ApiException;
import org.opensubtitles.model.Login200Response;
import org.opensubtitles.model.LoginRequest;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class OpenSubtitlesApi implements SubtitleApi {

    private final String apikey = "lNNp0yv0ah8gytkmYPbHwuaATJqr4rS9";
    @Getter
    private final Manager manager;
    private final ApiClient apiClient;

    public OpenSubtitlesApi(Manager manager) {
        this.manager = manager;
        apiClient = new ApiClient();
        apiClient.setApiKey(apikey);
    }

    public OpenSubtitlesApi(Manager manager, String userName, String password) throws OpenSubtitlesException {
        this(manager);
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
        return new SearchSubtitles(manager, apiClient);
    }

    public DownloadSubtitle downloadSubtitle() {
        return new DownloadSubtitle(apiClient);
    }

    public List<OpensubtitleSerieId> getProviderSerieIds(String serieName) throws OpenSubtitlesException {
        try {
            JSONArray shows = manager.getPageContentBuilder()
                    .url("https://www.opensubtitles.org/libs/suggest.php?format=json3&MovieName="
                            + URLEncoder.encode(serieName.toLowerCase(), StandardCharsets.UTF_8))
                    .userAgent("")
                    .cacheType(CacheType.MEMORY)
                    .retries(1)
                    .retryPredicate(exception -> exception instanceof HttpClientException e && e.getResponseCode() == 429)
                    .retryWait(5)
                    .getAsJsonArray();
            return StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize((Iterator<JSONObject>) (Iterator) shows.iterator(), Spliterator.ORDERED), false)
                    .filter(show -> "tv".equals(show.getString("kind")))
                    .map(show -> new OpensubtitleSerieId(show.getString("name"), show.getInt("id"), show.getString("year")))
                    .toList();
        } catch (Exception e) {
            throw new OpenSubtitlesException(e);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.OPENSUBTITLES;
    }
}
