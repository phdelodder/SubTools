package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class JPodnapisiAdapter extends AbstractAdapter<PodnapisiSubtitleDescriptor, ProviderSerieId, PodnapisiException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiAdapter.class);
    private static LazySupplier<JPodnapisiApi> jpapi;

    public JPodnapisiAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (jpapi == null) {
            jpapi = new LazySupplier<>(() -> {
                try {
                    return new JPodnapisiApi("JBierSubDownloader", manager);
                } catch (Exception e) {
                    throw new SubtitlesProviderInitException(getProviderName(), e);
                }
            });
        }
    }

    private JPodnapisiApi getApi() {
        return jpapi.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.PODNAPISI;
    }

    @Override
    public String getProviderName() {
        return getSubtitleSource().name();
    }

    @Override
    public List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws PodnapisiException {
        return getApi().getSubtitles(new String[] { hash }, language);
    }

    @Override
    public List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws PodnapisiException {
        return List.of();
    }

    @Override
    public List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language) throws PodnapisiException {
        return getApi().getMovieSubtitles(name, year, 0, 0, language);
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<PodnapisiSubtitleDescriptor> subtitles, Language language) {
        return buildListSubtitles(language, subtitles);
    }

    @Override
    public Set<PodnapisiSubtitleDescriptor> searchSerieSubtitles(TvRelease tvRelease, Language language) throws PodnapisiException {
        return getProviderSerieId(tvRelease.getOriginalName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId())
                .orElseMap(() -> getProviderSerieId(tvRelease.getName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId()))
                .map(providerSerieId -> tvRelease.getEpisodeNumbers().stream()
                        .flatMap(episode -> {
                            try {
                                return getApi().getSerieSubtitles(providerSerieId, tvRelease.getSeason(), episode, language).stream();
                            } catch (PodnapisiException e) {
                                LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
                                        TvRelease.formatName(providerSerieId.getProviderName(), tvRelease.getSeason(), episode),
                                        e.getMessage()), e);
                                return Stream.empty();
                            }
                        })
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<PodnapisiSubtitleDescriptor> subtitles, Language language) {
        return buildListSubtitles(language, subtitles);
    }

    private Set<Subtitle> buildListSubtitles(Language language, Collection<PodnapisiSubtitleDescriptor> lSubtitles) {
        return lSubtitles.stream()
                .filter(ossd -> StringUtils.isNotBlank(ossd.getReleaseString()))
                .map(ossd -> Subtitle.downloadSource(ossd.getUrl())
                        .subtitleSource(getSubtitleSource())
                        .fileName(ossd.getReleaseString())
                        .language(language)
                        .quality(ReleaseParser.getQualityKeyword(ossd.getReleaseString()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(ossd.getReleaseString(),
                                FilenameUtils.isExtension(ossd.getReleaseString(), "srt")))
                        .uploader(ossd.getUploaderName())
                        .hearingImpaired(ossd.isHearingImpaired()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<ProviderSerieId> getSortedProviderSerieIds(String serieName, int season) throws PodnapisiException {
        return getApi().getPodnapisiShowName(serieName).stream().toList();
    }

    @Override
    public boolean useSeasonForSerieId() {
        return false;
    }

    @Override
    public String providerSerieIdToDisplayString(ProviderSerieId providerSerieId) {
        return providerSerieId.getName();
    }
}
