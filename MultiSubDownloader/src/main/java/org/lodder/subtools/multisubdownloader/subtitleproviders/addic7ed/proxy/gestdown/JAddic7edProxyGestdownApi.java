package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import extensions.java.lang.String.StringExt;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.gestdown.api.SubtitlesApi;
import org.gestdown.api.TvShowsApi;
import org.gestdown.invoker.ApiException;
import org.gestdown.model.EpisodeDto;
import org.gestdown.model.SubtitleDto;
import org.gestdown.model.SubtitleSearchResponse;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;

// see https://www.gestdown.info/Api
public class JAddic7edProxyGestdownApi implements SubtitleApi {

    private static final String DOMAIN = "https://api.gestdown.info";

    private final Manager manager;
    private final TvShowsApi tvShowsApi = new TvShowsApi();
    private final SubtitlesApi subtitlesApi = new SubtitlesApi();
    @val @override SubtitleSource subtitleSource = SubtitleSource.ADDIC7ED;

    public JAddic7edProxyGestdownApi(Manager manager) {
        this.manager = manager;
    }

    public List<ProviderSerieId> getProviderSerieName(String serieName) throws ApiException {
        return tvShowsApi.showsSearchSearchGet(serieName).getShows().stream()
            .map(showDto -> new ProviderSerieId(showDto.getName(), showDto.getId().toString())).toList();
    }

    public List<ProviderSerieId> getProviderSerieName(int tvdbId) throws ApiException {
        return tvShowsApi.showsExternalTvdbTvdbIdGet(tvdbId).getShows().stream()
            .map(showDto -> new ProviderSerieId(showDto.getName(), showDto.getId().toString())).toList();
    }

    public Set<Subtitle> getSubtitles(SerieMapping providerSerieId, int season, int episode, Language language)
        throws ApiException {
        return manager.getCache(CacheType.MEMORY, "%s-subtitles-%s-%s-%s-%s".formatted(subtitleSource.name(),
                providerSerieId.providerId, season, episode, language))
            .getCollection(() -> {
                SubtitleSearchResponse response = subtitlesApi.subtitlesGetShowUniqueIdSeasonEpisodeLanguageGet(
                    language.getName(), UUID.fromString(providerSerieId.providerId), season, episode);
                return response.getMatchingSubtitles()
                    .stream()
                    .filter(SubtitleDto::isCompleted)
                    .map(sub -> mapToSubtitle(sub, response.episode, language))
                    .collect(Collectors.toSet());
            });
    }

    private Subtitle mapToSubtitle(SubtitleDto sub, EpisodeDto episodedto, Language language) {
        return new Subtitle(
            downloadSource:Subtitle.DownloadSource.of(getDownloadUrl(sub.getDownloadUri())),
            subtitleSource:subtitleSource,
            fileName:StringExt.removeIllegalFilenameChars("${episodedto.show} - ${episodedto.title} - ${sub.version}"),
            language:language,
            quality:ReleaseParser.getQualityKeyword(episodedto.getTitle() + " " + sub.getVersion()),
            subtitleMatchType:SubtitleMatchType.EVERYTHING,
            releaseGroup:sub.getVersion(),
            uploader:"",
            hearingImpaired:false);
    }

    public String getDownloadUrl(String subtitleId) {
        return DOMAIN + subtitleId;
    }
}
