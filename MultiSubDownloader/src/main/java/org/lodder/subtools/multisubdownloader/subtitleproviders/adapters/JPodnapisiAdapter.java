package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.JPodnapisiApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception.PodnapisiException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPodnapisiAdapter implements SubtitleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiAdapter.class);
    private static JPodnapisiApi jpapi;

    public JPodnapisiAdapter(Manager manager) {
        try {
            if (jpapi == null) {
                jpapi = new JPodnapisiApi("JBierSubDownloader", manager);
            }
        } catch (Exception e) {
            LOGGER.error("API Podnapisi INIT", e.getCause());
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.PODNAPISI;
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        List<PodnapisiSubtitleDescriptor> lSubtitles = new ArrayList<>();
        if (!"".equals(movieRelease.getFileName())) {
            File file = new File(movieRelease.getPath(), movieRelease.getFileName());
            if (file.exists()) {
                try {
                    lSubtitles = jpapi.searchSubtitles(new String[] { OpenSubtitlesHasher.computeHash(file) }, language);
                } catch (PodnapisiException e) {
                    LOGGER.error(
                            "API Podnapisi searchSubtitles using file hash for movie [%s] (%s)".formatted(movieRelease.getName(), e.getMessage()),
                            e);
                } catch (IOException e) {
                    LOGGER.error("Error calculating file hash", e);
                }
            }
        }
        if (lSubtitles.size() == 0) {
            try {
                lSubtitles.addAll(jpapi.searchSubtitles(movieRelease.getName(), movieRelease.getYear(), 0, 0, language));
            } catch (PodnapisiException e) {
                LOGGER.error("API Podnapisi searchSubtitles using name for movie [%s] (%s)".formatted(movieRelease.getName(), e.getMessage()), e);
            }
        }
        return buildListSubtitles(language, lSubtitles);
    }

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {
        String showName = StringUtils.isNotBlank(tvRelease.getOriginalShowName()) ? tvRelease.getOriginalShowName() : tvRelease.getName();
        List<PodnapisiSubtitleDescriptor> lSubtitles = new ArrayList<>();
        for (int episode : tvRelease.getEpisodeNumbers()) {
            try {
                jpapi.searchSubtitles(showName, 0, tvRelease.getSeason(), episode, language).forEach(lSubtitles::add);
            } catch (PodnapisiException e) {
                LOGGER.error("API Podnapisi searchSubtitles using name for serie [%s] (%s)"
                        .formatted(TvRelease.formatName(tvRelease.getName(), tvRelease.getSeason(), episode), e.getMessage()), e);
            }
        }
        return buildListSubtitles(language, lSubtitles);
    }

    private Set<Subtitle> buildListSubtitles(Language language, List<PodnapisiSubtitleDescriptor> lSubtitles) {
        return lSubtitles.stream()
                .filter(ossd -> !"".equals(ossd.getReleaseString()))
                .map(ossd -> Subtitle.downloadSource(ossd.getUrl())
                        .subtitleSource(getSubtitleSource())
                        .fileName(ossd.getReleaseString())
                        .language(language)
                        .quality(ReleaseParser.getQualityKeyword(ossd.getReleaseString()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(ossd.getReleaseString(),
                                FilenameUtils.isExtension(ossd.getReleaseString(), "srt")))
                        .uploader(ossd.getUploaderName())
                        .hearingImpaired(ossd.getFlagsString().contains("n")))
                .collect(Collectors.toSet());
    }
}
