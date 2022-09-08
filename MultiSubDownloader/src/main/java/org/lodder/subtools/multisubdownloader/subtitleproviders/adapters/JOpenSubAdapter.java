package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.OpenSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.exception.OpenSubtitlesException;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.opensubtitles.invoker.ApiException;
import org.opensubtitles.model.Latest200ResponseDataInnerAttributesFilesInner;
import org.opensubtitles.model.SubtitleAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pivovarit.function.ThrowingSupplier;

public class JOpenSubAdapter implements SubtitleProvider {

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
            LOGGER.error("API OPENSUBTITLES INIT", e);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.OPENSUBTITLES;
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String languageId) {
        List<org.opensubtitles.model.Subtitle> subtitles = new ArrayList<>();
        if (!"".equals(movieRelease.getFilename())) {
            File file = new File(movieRelease.getPath(), movieRelease.getFilename());
            if (file.exists()) {
                try {
                    osApi.searchSubtitles()
                            .movieHash(OpenSubtitlesHasher.computeHash(file))
                            .language(languageId)
                            .searchSubtitles()
                            .getData().forEach(subtitles::add);
                } catch (ApiException e) {
                    LOGGER.error("API OPENSUBTITLES searchSubtitles using file hash", e);
                } catch (IOException e) {
                    LOGGER.error("Error calculating file hash", e);
                }
            }
        }
        if (movieRelease.getImdbId() != 0) {
            try {
                osApi.searchSubtitles()
                        .imdbId(movieRelease.getImdbId())
                        .language(languageId)
                        .searchSubtitles()
                        .getData().forEach(subtitles::add);
            } catch (ApiException e) {
                LOGGER.error("API OPENSUBTITLES searchSubtitles using imdbid", e);
            }
        }
        if (subtitles.isEmpty()) {
            try {
                osApi.searchSubtitles()
                        .query(movieRelease.getTitle())
                        .language(languageId)
                        .searchSubtitles()
                        .getData().forEach(subtitles::add);
            } catch (ApiException e) {
                LOGGER.error("API OPENSUBTITLES searchSubtitles using title", e);
            }
        }
        return subtitles.stream().map(org.opensubtitles.model.Subtitle::getAttributes)
                .filter(attributes -> movieRelease.getYear() == attributes.getFeatureDetails().getYear().intValue())
                .flatMap(attributes -> attributes.getFiles().stream().map(file -> createSubtitle(file, attributes)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtitle> searchSubtitles(TvRelease tvRelease, String languageId) {
        List<org.opensubtitles.model.Subtitle> subtitles = new ArrayList<>();
        if (tvRelease.getOriginalShowName().length() > 0) {
            tvRelease.getEpisodeNumbers().forEach(episode -> {
                try {
                    osApi.searchSubtitles()
                            .query(tvRelease.getOriginalShowName())
                            .season(tvRelease.getSeason())
                            .episode(episode)
                            .language(languageId)
                            .searchSubtitles()
                            .getData().forEach(subtitles::add);
                } catch (ApiException e) {
                    LOGGER.error("API OPENSUBTITLES searchSubtitles using title", e);
                }
            });
        }
        if (tvRelease.getOriginalShowName().length() == 0 || !tvRelease.getOriginalShowName().equalsIgnoreCase(tvRelease.getShowName())) {
            tvRelease.getEpisodeNumbers().forEach(episode -> {
                try {
                    osApi.searchSubtitles()
                            .query(tvRelease.getShowName())
                            .season(tvRelease.getSeason())
                            .episode(episode)
                            .language(languageId)
                            .searchSubtitles()
                            .getData().forEach(subtitles::add);
                } catch (ApiException e) {
                    LOGGER.error("API OPENSUBTITLES searchSubtitles using title", e);
                }
            });
        }

        String name = tvRelease.getShowName().replaceAll("[^A-Za-z]", "").toLowerCase();
        String originalName = tvRelease.getOriginalShowName().replaceAll("[^A-Za-z]", "").toLowerCase();

        return subtitles.stream().map(org.opensubtitles.model.Subtitle::getAttributes)
                .flatMap(attributes -> attributes.getFiles().stream()
                        .filter(file -> {
                            String subFileName = file.getFileName().replaceAll("[^A-Za-z]", "").toLowerCase();
                            return subFileName.contains(name) || originalName.length() > 0 && subFileName.contains(originalName);
                        })
                        .map(file -> createSubtitle(file, attributes)))
                .collect(Collectors.toList());
    }

    private Subtitle createSubtitle(Latest200ResponseDataInnerAttributesFilesInner file, SubtitleAttributes attributes) {
        ThrowingSupplier<String, ManagerException> urlSupplier = () -> {
            try {
                return osApi.downloadSubtitle().fileId(file.getFileId().intValue()).download().getLink();
            } catch (ApiException e) {
                throw new ManagerException(e);
            }
        };
        return Subtitle.downloadSource(urlSupplier)
                .subtitleSource(getSubtitleSource())
                .fileName(file.getFileName())
                .languageCode(attributes.getLanguage())
                .quality(ReleaseParser.getQualityKeyword(file.getFileName()))
                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                .releaseGroup(ReleaseParser.extractReleasegroup(file.getFileName(), FilenameUtils.isExtension(file.getFileName(), "srt")))
                .uploader(attributes.getUploader().getName())
                .hearingImpaired(attributes.isHearingImpaired());
    }
}
