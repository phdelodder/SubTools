package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.exception.OpenSubtitlesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpensubtitleSerieId;
import org.lodder.subtools.sublibrary.Credentials;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.opensubtitles.model.SubtitleAttributes;
import org.opensubtitles.model.SubtitleAttributesFilesInner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public final class JOpenSubAdapter
    extends AbstractAdapter<org.opensubtitles.model.Subtitle, OpensubtitleSerieId, OpenSubtitlesException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JOpenSubAdapter.class);

    private static LazySupplier<OpenSubtitlesApi> osApi;
    @val @override SubtitleSource subtitleSource = SubtitleSource.OPENSUBTITLES;
    @val @override String providerName = subtitleSource.name();
    @val @override boolean useSeasonForSerieId = false;

    public JOpenSubAdapter(Manager manager, Credentials credentials, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        if (osApi == null) {
            osApi = new LazySupplier<>(() -> {
                try {
                    return new OpenSubtitlesApi(manager, credentials);
                } catch (OpenSubtitlesException e) {
                    throw new SubtitlesProviderInitException(providerName, e);
                }
            });
        }
    }

    private OpenSubtitlesApi getApi() {
        return osApi.get();
    }

    @Override
    public List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithHash(String hash, Language language)
        throws OpenSubtitlesException {
        return getApi().searchSubtitles().movieHash(hash).language(language).searchSubtitles().getData();
    }

    @Override
    public List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithId(int tvdbId, Language language)
        throws OpenSubtitlesException {
        return getApi().searchSubtitles().imdbId(tvdbId).language(language).searchSubtitles().getData();
    }

    @Override
    public Collection<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithName(String name,
        @Nullable Integer year,
        Language language) throws OpenSubtitlesException {
        return getApi().searchSubtitles().query(name).language(language).searchSubtitles().getData();
    }

    @Override
    public Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<org.opensubtitles.model.Subtitle> subtitles,
        Language language) {
        return subtitles.stream()
            .map(org.opensubtitles.model.Subtitle::getAttributes)
            .filter(attributes ->
                attributes.getFeatureDetails().getYear() != null
                    ? Objects.equals(attributes.getFeatureDetails().getYear().intValue(), movieRelease.year)
                    : movieRelease.year == null)
            .flatMap(attributes -> attributes.getFiles().stream().map(file -> createSubtitle(file, attributes)))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<org.opensubtitles.model.Subtitle> searchSerieSubtitles(TvRelease tvRelease, Language language)
        throws OpenSubtitlesException {
        return getProviderSerieId(tvRelease).map(
            providerSerieId -> tvRelease.episodes.stream().flatMap(episode -> {
                try {
                    return getApi().searchSubtitles()
                        .query(providerSerieId.name)
                        .season(tvRelease.season)
                        .episode(episode)
                        .language(language)
                        .searchSubtitles()
                        .getData()
                        .stream();
                } catch (OpenSubtitlesException e) {
                    LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(subtitleSource.name,
                        TvRelease.formatName(providerSerieId.providerName, tvRelease.season, episode),
                        e.getMessage()), e);
                    return Stream.empty();
                }
            }).collect(Collectors.toSet())).orElseGet(Set::of);
    }

    @Override
    public Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<org.opensubtitles.model.Subtitle> subtitles,
        Language language) {
        String name = StringUtils.lowerCase(RegExUtils.replaceAll(tvRelease.name, "[^A-Za-z]", ""));
        String originalName = StringUtils.lowerCase(RegExUtils.replaceAll(tvRelease.originalName, "[^A-Za-z]", ""));
        return subtitles.stream()
            .map(org.opensubtitles.model.Subtitle::getAttributes)
            .flatMap(attributes -> attributes.getFiles().stream().filter(file -> {
                String subFileName = file.getFileName().replaceAll("[^A-Za-z]", "").toLowerCase();
                return subFileName.contains(name) ||
                    (StringUtils.isNotBlank(originalName) && subFileName.contains(originalName));
            }).map(file -> createSubtitle(file, attributes)))
            .collect(Collectors.toSet());
    }

    private Subtitle createSubtitle(SubtitleAttributesFilesInner file, SubtitleAttributes attributes) {
        return new Subtitle(
            downloadSource:Subtitle.DownloadSource.of(
                () -> getApi().downloadSubtitle().fileId(file.getFileId().intValue()).download().getLink()),
            subtitleSource:subtitleSource,
            fileName:file.getFileName(),
            language:Language.fromIdOptional(attributes.getLanguage()).orElse(null),
            quality:ReleaseParser.getQualityKeyword(file.getFileName()),
            subtitleMatchType:SubtitleMatchType.EVERYTHING,
            releaseGroup:ReleaseParser.extractReleaseGroup(file.fileName, file.fileName.endsWith(".srt")),
            uploader:attributes.getUploader() != null ? attributes.getUploader().getName() : null,
            hearingImpaired:Boolean.TRUE == attributes.isHearingImpaired());
    }

    @Override
    public List<OpensubtitleSerieId> getSortedProviderSerieIds(@Nullable Integer tvdbId, String serieName, int season)
        throws OpenSubtitlesException {
        return getApi().getProviderSerieIds(serieName)
            .stream()
            .sorted(
                Comparator.comparing((OpensubtitleSerieId n) -> !serieName.replaceAll("[^A-Za-z]", "")
                    .equalsIgnoreCase(n.name.replaceAll("[^A-Za-z]", "")))
                    .thenComparing(OpensubtitleSerieId::getYear, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    }

    @Override
    public String providerSerieIdToDisplayString(OpensubtitleSerieId providerSerieId) {
        return "${providerSerieId.name} (${providerSerieId.year})";
    }
}
