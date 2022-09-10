package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.JPodnapisiApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
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
            LOGGER.error("API PODNAPISI INIT", e.getCause());
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.PODNAPISI;
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        List<PodnapisiSubtitleDescriptor> lSubtitles = new ArrayList<>();
        if (!"".equals(movieRelease.getFilename())) {
            File file = new File(movieRelease.getPath(), movieRelease.getFilename());
            if (file.exists()) {
                try {
                    lSubtitles = jpapi.searchSubtitles(new String[] { OpenSubtitlesHasher.computeHash(file) }, language);
                } catch (IOException | XmlRpcException e) {
                    LOGGER.error("API PODNAPISI searchSubtitles using file hash", e);
                }
            }
        }
        if (lSubtitles.size() == 0) {
            lSubtitles.addAll(jpapi.searchSubtitles(movieRelease.getTitle(), movieRelease.getYear(), 0, 0, language));
        }
        return buildListSubtitles(language, lSubtitles);
    }

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {

        String showName = tvRelease.getOriginalShowName().length() > 0 ? tvRelease.getOriginalShowName() : tvRelease.getShowName();
        List<PodnapisiSubtitleDescriptor> lSubtitles;
        if (showName.length() > 0) {
            lSubtitles = tvRelease.getEpisodeNumbers().stream()
                    .flatMap(episode -> jpapi.searchSubtitles(showName, 0, tvRelease.getSeason(), episode, language).stream())
                    .collect(Collectors.toList());
        } else {
            lSubtitles = new ArrayList<>();
        }
        return buildListSubtitles(language, lSubtitles);
    }

    private Set<Subtitle> buildListSubtitles(Language language, List<PodnapisiSubtitleDescriptor> lSubtitles) {
        return lSubtitles.stream()
                .filter(ossd -> !"".equals(ossd.getReleaseString()))
                .map(ossd -> getDownloadLink(ossd.getSubtitleId())
                        .map(downloadlink -> Subtitle.downloadSource(downloadlink)
                                .subtitleSource(getSubtitleSource())
                                .fileName(ossd.getReleaseString())
                                .language(language)
                                .quality(ReleaseParser.getQualityKeyword(ossd.getReleaseString()))
                                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                                .releaseGroup(ReleaseParser.extractReleasegroup(ossd.getReleaseString(),
                                        FilenameUtils.isExtension(ossd.getReleaseString(), "srt")))
                                .uploader(ossd.getUploaderName())
                                .hearingImpaired("nhu".equals(ossd.getFlagsString())))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Optional<String> getDownloadLink(String subtitleId) {
        try {
            return Optional.of(jpapi.downloadUrl(subtitleId));
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI getdownloadlink", e);
        }
        return Optional.empty();
    }
}
