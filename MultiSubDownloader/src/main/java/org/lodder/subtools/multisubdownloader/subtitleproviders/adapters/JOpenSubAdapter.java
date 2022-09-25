package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.OpenSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.exception.OpenSubtitlesException;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.opensubtitles.model.Latest200ResponseDataInnerAttributesFilesInner;
import org.opensubtitles.model.SubtitleAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JOpenSubAdapter extends AbstractAdapter<org.opensubtitles.model.Subtitle, OpenSubtitlesException> {

    private static OpenSubtitlesApi osApi;
    private static final Logger LOGGER = LoggerFactory.getLogger(JOpenSubAdapter.class);

    public JOpenSubAdapter(boolean isLoginEnabled, String username, String password, Manager manager) {
        try {
            if (osApi == null) {
                if (isLoginEnabled) {
                    osApi = new OpenSubtitlesApi(username, password);
                } else {
                    osApi = new OpenSubtitlesApi();
                }
            }
        } catch (OpenSubtitlesException e) {
            LOGGER.error("API OpenSubtitles INIT (%s)".formatted(e.getMessage()), e);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.OPENSUBTITLES;
    }

    @Override
    protected List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithHash(String hash, Language language) throws OpenSubtitlesException {
        return osApi.searchSubtitles()
                .movieHash(hash)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    protected List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithId(int tvdbId, Language language) throws OpenSubtitlesException {
        return osApi.searchSubtitles()
                .imdbId(tvdbId)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    protected List<org.opensubtitles.model.Subtitle> searchMovieSubtitlesWithName(String name, int year, Language language)
            throws OpenSubtitlesException {
        return osApi.searchSubtitles()
                .query(name)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<org.opensubtitles.model.Subtitle> subtitles, Language language) {
        return subtitles.stream().map(org.opensubtitles.model.Subtitle::getAttributes)
                .filter(attributes -> movieRelease.getYear() == attributes.getFeatureDetails().getYear().intValue())
                .flatMap(attributes -> attributes.getFiles().stream().map(file -> createSubtitle(file, attributes)))
                .collect(Collectors.toSet());
    }


    @Override
    protected List<org.opensubtitles.model.Subtitle> searchSerieSubtitles(String name, int season, int episode, Language language)
            throws OpenSubtitlesException {
        return osApi.searchSubtitles()
                .query(name)
                .season(season)
                .episode(episode)
                .language(language)
                .searchSubtitles()
                .getData();
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Set<org.opensubtitles.model.Subtitle> subtitles, Language language) {
        String name = tvRelease.getName().replaceAll("[^A-Za-z]", "").toLowerCase();
        String originalName = tvRelease.getOriginalShowName().replaceAll("[^A-Za-z]", "").toLowerCase();
        return subtitles.stream().map(org.opensubtitles.model.Subtitle::getAttributes)
                .flatMap(attributes -> attributes.getFiles().stream()
                        .filter(file -> {
                            String subFileName = file.getFileName().replaceAll("[^A-Za-z]", "").toLowerCase();
                            return subFileName.contains(name) || StringUtils.isNotBlank(originalName) && subFileName.contains(originalName);
                        })
                        .map(file -> createSubtitle(file, attributes)))
                .collect(Collectors.toSet());
    }

    private Subtitle createSubtitle(Latest200ResponseDataInnerAttributesFilesInner file, SubtitleAttributes attributes) {
        return Subtitle.downloadSource(() -> osApi.downloadSubtitle().fileId(file.getFileId().intValue()).download().getLink())
                .subtitleSource(getSubtitleSource())
                .fileName(file.getFileName())
                .language(Language.fromIdOptional(attributes.getLanguage()).orElse(null))
                .quality(ReleaseParser.getQualityKeyword(file.getFileName()))
                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                .releaseGroup(ReleaseParser.extractReleasegroup(file.getFileName(), FilenameUtils.isExtension(file.getFileName(), "srt")))
                .uploader(attributes.getUploader().getName())
                .hearingImpaired(attributes.isHearingImpaired());
    }
}
