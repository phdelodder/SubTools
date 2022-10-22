package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gestdown.api.SubtitlesApi;
import org.gestdown.api.TvShowsApi;
import org.gestdown.invoker.ApiException;
import org.gestdown.model.EpisodeDto;
import org.gestdown.model.ShowSearchRequest;
import org.gestdown.model.SubtitleDto;
import org.gestdown.model.SubtitleSearchResponse;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.StringUtil;

import lombok.experimental.ExtensionMethod;

// see https://www.gestdown.info/Api
@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edProxyGestdownApi extends Html implements SubtitleApi {

    private static final String DOMAIN = "https://api.gestdown.info";
    private final TvShowsApi tvShowsApi;
    private final SubtitlesApi subtitlesApi;

    public JAddic7edProxyGestdownApi(Manager manager) {
        super(manager);
        tvShowsApi = new TvShowsApi();
        subtitlesApi = new SubtitlesApi();
    }

    public List<ProviderSerieId> getProviderSerieName(String serieName) throws ApiException {
        try {
            return tvShowsApi.showsSearchPost(new ShowSearchRequest().query(serieName)).getShows().stream()
                    .map(showDto -> new ProviderSerieId(serieName, showDto.getName())).toList();
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    public Set<Subtitle> getSubtitles(SerieMapping providerSerieId, int season, int episode, Language language) throws ApiException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-subtitles-%s-%s-%s-%s".formatted(getSubtitleSource().name(), providerSerieId.getProviderId(), season, episode, language))
                .collectionSupplier(Subtitle.class, () -> {
                    Set<Subtitle> results = new HashSet<>();
                    SubtitleSearchResponse response = subtitlesApi.subtitlesFindLanguageShowSeasonEpisodeGet(language.getName(),
                            providerSerieId.getProviderId(), season, episode);
                    response.getMatchingSubtitles().stream()
                            .filter(SubtitleDto::isCompleted).map(sub -> mapToSubtitle(sub, response.getEpisode(), language))
                            .forEach(results::add);
                    return results;
                }).getCollection();
    }

    private Subtitle mapToSubtitle(SubtitleDto sub, EpisodeDto episodedto, Language language) {
        return Subtitle.downloadSource(getDownloadUrl(sub.getDownloadUri()))
                .subtitleSource(getSubtitleSource())
                .fileName(StringUtil
                        .removeIllegalFilenameChars("%s - %s - %s".formatted(episodedto.getShow(), episodedto.getTitle(), sub.getVersion())))
                .language(language)
                .quality(ReleaseParser.getQualityKeyword(episodedto.getTitle() + " " + sub.getVersion()))
                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                .releaseGroup(sub.getVersion())
                .uploader("")
                .hearingImpaired(false);
    }

    public String getDownloadUrl(String subtitleId) {
        return DOMAIN + subtitleId;
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }
}
