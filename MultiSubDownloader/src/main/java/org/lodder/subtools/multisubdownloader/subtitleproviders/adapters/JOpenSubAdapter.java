package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.exception.OpenSubtitlesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpensubtitleSerieId;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.opensubtitles.model.Latest200ResponseDataInnerAttributesFilesInner;
import org.opensubtitles.model.SubtitleAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class JOpenSubAdapter
        extends AbstractAdapter<org.opensubtitles.model.Subtitle, OpensubtitleSerieId, OpenSubtitlesException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JOpenSubAdapter.class);
    private static LazySupplier<OpenSubtitlesApi> osApi;

    public JOpenSubAdapter(boolean isLoginEnabled, String username, String password, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (osApi == null) {
            osApi = new LazySupplier<>(() -> {
                try {
                    if (isLoginEnabled) {
                        return new OpenSubtitlesApi(manager, username, password);
                    } else {
                        return new OpenSubtitlesApi(manager);
                    }
                } catch (OpenSubtitlesException e) {
                    throw new SubtitlesProviderInitException(getProviderName(), e);
                }
            });
        }
    }

    private OpenSubtitlesApi getApi() {
        return osApi.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.OPENSUBTITLES;
    }

    @Override
    public String getProviderName() {
        return getSubtitleSource().name();
    }

    @Override
    public List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithHash(String hash, Language language) throws OpenSubtitlesException {
        return getApi().searchSubtitles()
                .movieHash(hash)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    public List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithId(int tvdbId, Language language) throws OpenSubtitlesException {
        return getApi().searchSubtitles()
                .imdbId(tvdbId)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    public List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithName(String name, int year, Language language)
            throws OpenSubtitlesException {
        return getApi().searchSubtitles()
                .query(name)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<org.opensubtitles.model.Subtitle> subtitles, Language language) {
        return subtitles.stream().map(org.opensubtitles.model.Subtitle::getAttributes)
                .filter(attributes -> movieRelease.getYear() == attributes.getFeatureDetails().getYear().intValue())
                .flatMap(attributes -> attributes.getFiles().stream().map(file -> createSubtitle(file, attributes)))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<org.opensubtitles.model.Subtitle> searchSerieSubtitles(TvRelease tvRelease, Language language) throws OpenSubtitlesException {
        return getProviderSerieId(tvRelease.getOriginalName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId())
                .orElseMap(() -> getProviderSerieId(tvRelease.getName(), tvRelease.getDisplayName(), tvRelease.getSeason(), tvRelease.getTvdbId()))
                .map(providerSerieId -> tvRelease.getEpisodeNumbers().stream()
                        .flatMap(episode -> {
                            try {
                                return getApi().searchSubtitles()
                                        .query(providerSerieId.getName())
                                        .season(tvRelease.getSeason())
                                        .episode(episode)
                                        .language(language)
                                        .searchSubtitles()
                                        .getData().stream();
                            } catch (OpenSubtitlesException e) {
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
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<org.opensubtitles.model.Subtitle> subtitles, Language language) {
        String name = tvRelease.getName().replaceAll("[^A-Za-z]", "").toLowerCase();
        String originalName = tvRelease.getOriginalName().replaceAll("[^A-Za-z]", "").toLowerCase();
        return subtitles.stream().map(org.opensubtitles.model.Subtitle::getAttributes)
                .flatMap(attributes -> attributes.getFiles().stream()
                        .filter(file -> {
                            String subFileName = file.getFileName().replaceAll("[^A-Za-z]", "").toLowerCase();
                            return subFileName.contains(name) || (StringUtils.isNotBlank(originalName) && subFileName.contains(originalName));
                        })
                        .map(file -> createSubtitle(file, attributes)))
                .collect(Collectors.toSet());
    }

    private Subtitle createSubtitle(Latest200ResponseDataInnerAttributesFilesInner file, SubtitleAttributes attributes) {
        return Subtitle.downloadSource(() -> getApi().downloadSubtitle().fileId(file.getFileId().intValue()).download().getLink())
                .subtitleSource(getSubtitleSource())
                .fileName(file.getFileName())
                .language(Language.fromIdOptional(attributes.getLanguage()).orElse(null))
                .quality(ReleaseParser.getQualityKeyword(file.getFileName()))
                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                .releaseGroup(ReleaseParser.extractReleasegroup(file.getFileName(), FilenameUtils.isExtension(file.getFileName(), "srt")))
                .uploader(attributes.getUploader().getName())
                .hearingImpaired(attributes.isHearingImpaired());
    }

    @Override
    public List<OpensubtitleSerieId> getSortedProviderSerieIds(String serieName, int season) throws OpenSubtitlesException {
        return getApi().getProviderSerieIds(serieName).stream()
                .sorted(Comparator.comparing(
                        (OpensubtitleSerieId n) -> !serieName.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(n.getName().replaceAll("[^A-Za-z]", "")))
                        .thenComparing(OpensubtitleSerieId::getYear, Comparator.reverseOrder()))
                .toList();
    }

    @Override
    public boolean useSeasonForSerieId() {
        return false;
    }

    @Override
    public String providerSerieIdToDisplayString(OpensubtitleSerieId providerSerieId) {
        return "%s (%s)".formatted(providerSerieId.getName(), providerSerieId.getYear());
    }
}
