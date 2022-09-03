package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.JPodnapisiApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPodnapisiAdapter implements JSubAdapter, SubtitleProvider {

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
    public String getName() {
        return "Podnapisi";
    }

    @Override
    public List<Subtitle> search(Release release, String languageCode) {
        if (release instanceof MovieRelease movieRelease) {
            return this.searchSubtitles(movieRelease, languageCode);
        } else if (release instanceof TvRelease tvRelease) {
            return this.searchSubtitles(tvRelease, languageCode);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageid) {
        List<PodnapisiSubtitleDescriptor> lSubtitles = new ArrayList<>();
        if (!"".equals(movieRelease.getFilename())) {
            File file = new File(movieRelease.getPath(), movieRelease.getFilename());
            if (file.exists()) {
                try {
                    lSubtitles = jpapi.searchSubtitles(new String[] { OpenSubtitlesHasher.computeHash(file) }, sublanguageid[0]);
                } catch (Exception e) {
                    LOGGER.error("API PODNAPISI searchSubtitles using file hash", e);
                }
            }
        }
        if (lSubtitles.size() == 0) {
            lSubtitles.addAll(jpapi.searchSubtitles(movieRelease.getTitle(), movieRelease.getYear(), 0, 0, sublanguageid[0]));
        }
        return buildListSubtitles(sublanguageid[0], lSubtitles);
    }

    @Override
    public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageid) {

        String showName = tvRelease.getOriginalShowName().length() > 0 ? tvRelease.getOriginalShowName() : tvRelease.getShow();
        List<PodnapisiSubtitleDescriptor> lSubtitles;
        if (showName.length() > 0) {
            lSubtitles = tvRelease.getEpisodeNumbers().stream()
                    .flatMap(episode -> jpapi.searchSubtitles(showName, 0, tvRelease.getSeason(), episode, sublanguageid[0]).stream())
                    .collect(Collectors.toList());
        } else {
            lSubtitles = new ArrayList<>();
        }
        return buildListSubtitles(sublanguageid[0], lSubtitles);
    }

    private List<Subtitle> buildListSubtitles(String sublanguageid, List<PodnapisiSubtitleDescriptor> lSubtitles) {
        List<Subtitle> listFoundSubtitles = new ArrayList<>();
        for (PodnapisiSubtitleDescriptor ossd : lSubtitles) {
            if (!"".equals(ossd.getReleaseString())) {
                String downloadlink = getDownloadLink(ossd.getSubtitleId());
                if (downloadlink != null) {
                    listFoundSubtitles.add(new Subtitle(
                            Subtitle.SubtitleSource.PODNAPISI,
                            ossd.getReleaseString(),
                            downloadlink,
                            sublanguageid,
                            ReleaseParser.getQualityKeyword(ossd.getReleaseString()),
                            SubtitleMatchType.EVERYTHING,
                            ReleaseParser.extractReleasegroup(ossd.getReleaseString(), FilenameUtils.isExtension(ossd.getReleaseString(), "srt")),
                            ossd.getUploaderName(),
                            "nhu".equals(ossd.getFlagsString())));
                }
            }
        }
        return listFoundSubtitles;
    }

    private String getDownloadLink(String subtitleId) {
        try {
            return jpapi.downloadUrl(subtitleId);
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI getdownloadlink", e);
        }
        return null;
    }
}
