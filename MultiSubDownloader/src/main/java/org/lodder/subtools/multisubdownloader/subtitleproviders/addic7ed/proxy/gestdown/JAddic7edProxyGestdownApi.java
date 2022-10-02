package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.proxy.gestdown;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.gestdown.api.SubtitlesApi;
import org.gestdown.api.TvShowsApi;
import org.gestdown.invoker.ApiException;
import org.gestdown.model.EpisodeDto;
import org.gestdown.model.ShowDto;
import org.gestdown.model.ShowSearchRequest;
import org.gestdown.model.SubtitleDto;
import org.gestdown.model.SubtitleSearchResponse;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
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

    public List<String> getSerieNameForName(String name) throws ApiException {
        return getValue("%s-SerieName-%s".formatted("Gestdown", name))
                .cacheType(CacheType.MEMORY)
                .collectionSupplier(String.class,
                        () -> tvShowsApi.showsSearchPost(new ShowSearchRequest().query(name)).getShows().stream().map(ShowDto::getName).toList())
                .getCollection();
    }

    public List<Subtitle> searchSubtitles(String showName, int season, int episode, Language language) throws ApiException {
        SubtitleSearchResponse response = subtitlesApi.subtitlesFindLanguageShowSeasonEpisodeGet(language.getName(), showName, season, episode);
        return response.getMatchingSubtitles().stream()
                .filter(SubtitleDto::isCompleted).map(sub -> mapToSubtitle(sub, response.getEpisode(), language))
                .toList();
    }

    private Subtitle mapToSubtitle(SubtitleDto sub, EpisodeDto episodedto, Language language) {
        return Subtitle.downloadSource(getDownloadUrl(sub.getDownloadUri()))
                .subtitleSource(getSubtitleSource())
                .fileName(StringUtil.removeIllegalFilenameChars(episodedto.getTitle() + " " + sub.getVersion()))
                .language(language)
                .quality(ReleaseParser.getQualityKeyword(episodedto.getTitle() + " " + sub.getVersion()))
                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                .releaseGroup(ReleaseParser.extractReleasegroup(episodedto.getTitle() + " " + sub.getVersion(),
                        FilenameUtils.isExtension(episodedto.getTitle() + " " + sub.getVersion(), "srt")))
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
