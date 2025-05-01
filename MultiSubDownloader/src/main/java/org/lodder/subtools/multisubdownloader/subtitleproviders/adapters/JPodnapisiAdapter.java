package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.JPodnapisiApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception.PodnapisiException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JPodnapisiAdapter
    extends AbstractAdapter<PodnapisiSubtitleDescriptor, ProviderSerieId, PodnapisiException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiAdapter.class);

    private static LazySupplier<JPodnapisiApi> jpapi;
    @val @override SubtitleSource subtitleSource = SubtitleSource.PODNAPISI;
    @val @override String providerName = subtitleSource.name();
    @val @override boolean useSeasonForSerieId = false;

    public JPodnapisiAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (jpapi == null) {
            jpapi = new LazySupplier<>(() -> {
                try {
                    return new JPodnapisiApi(manager, "JBierSubDownloader");
                } catch (Exception e) {
                    throw new SubtitlesProviderInitException(providerName, e);
                }
            });
        }
    }

    private JPodnapisiApi getApi() {
        return jpapi.get();
    }

    @Override
    public List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) {
        return List.of();
    }

    @Override
    public List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) {
        return List.of();
    }

    @Override
    public Collection<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithName(String name, @Nullable Integer year,
        Language language) throws PodnapisiException {
        return getApi().getMovieSubtitles(name, year, 0, 0, language);
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<PodnapisiSubtitleDescriptor> subtitles,
        Language language) {
        return buildListSubtitles(language, subtitles);
    }

    @Override
    public Set<PodnapisiSubtitleDescriptor> searchSerieSubtitles(TvRelease tvRelease, Language language)
        throws PodnapisiException {
        return getProviderSerieId(tvRelease).map(
            providerSerieId -> tvRelease.episodes.stream().flatMap(episode -> {
                try {
                    return api.getSerieSubtitles(providerSerieId, tvRelease.season, episode, language).stream();
                } catch (PodnapisiException e) {
                    LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(subtitleSource.name,
                        TvRelease.formatName(providerSerieId.providerName, tvRelease.season, episode),
                        e.getMessage()), e);
                    return Stream.empty();
                }
            }).collect(Collectors.toSet())).orElseGet(Set::of);
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<PodnapisiSubtitleDescriptor> subtitles,
        Language language) {
        return buildListSubtitles(language, subtitles);
    }

    private Set<Subtitle> buildListSubtitles(Language language, Collection<PodnapisiSubtitleDescriptor> lSubtitles) {
        return lSubtitles.stream()
            .filter(ossd -> StringUtils.isNotBlank(ossd.releaseString))
            .map(ossd -> new Subtitle(
                downloadSource:Subtitle.DownloadSource.of(ossd.url),
                subtitleSource:subtitleSource,
                fileName:ossd.releaseString,
                language:language,
                quality:ReleaseParser.getQualityKeyword(ossd.releaseString),
                subtitleMatchType:SubtitleMatchType.EVERYTHING,
                releaseGroup:ReleaseParser.extractReleaseGroup(ossd.releaseString,
                    StringUtils.endsWith(ossd.releaseString, ".srt")),
                uploader:ossd.uploaderName,
                hearingImpaired:ossd.hearingImpaired))
            .collect(Collectors.toSet());
    }

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(@Nullable Integer tvdbId, String serieName, int season)
        throws PodnapisiException {
        return getApi().getPodnapisiShowName(serieName).stream().toList();
    }

    @Override
    public String providerSerieIdToDisplayString(ProviderSerieId providerSerieId) {
        return providerSerieId.name;
    }
}
