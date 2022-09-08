package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtiltesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JTVsubtitlesAdapter implements SubtitleProvider {

    private static JTVSubtitlesApi jtvapi;
    private static final Logger LOGGER = LoggerFactory.getLogger(JTVsubtitlesAdapter.class);

    public JTVsubtitlesAdapter(Manager manager) {
        try {
            if (jtvapi == null) {
                jtvapi = new JTVSubtitlesApi(manager);
            }
        } catch (Exception e) {
            LOGGER.error("API JTVsubtitles INIT", e);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.TVSUBTITLES;
    }

    @Override
    public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
        List<TVsubtitlesSubtitleDescriptor> lSubtitles = new ArrayList<>();
        try {
            String showName = tvRelease.getOriginalShowName().length() > 0 ? tvRelease.getOriginalShowName() : tvRelease.getShow();

            if (showName.length() > 0) {
                if (showName.contains("(") && showName.contains(")")) {
                    String alterName = showName.substring(0, showName.indexOf("(") - 1).trim();
                    lSubtitles = jtvapi.searchSubtitles(alterName, tvRelease.getSeason(), tvRelease
                            .getEpisodeNumbers().get(0), tvRelease.getTitle(), sublanguageids[0]);
                }
                lSubtitles.addAll(jtvapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease
                        .getEpisodeNumbers().get(0), tvRelease.getTitle(), sublanguageids[0]));
            }
        } catch (TvSubtiltesException e) {
            LOGGER.error("API JTVsubtitles searchSubtitles using title", e);
        }

        return lSubtitles.stream()
                .map(sub -> Subtitle.downloadSource(sub.Url)
                        .subtitleSource(getSubtitleSource())
                        .fileName(sub.Filename)
                        .languageCode(sublanguageids[0])
                        .quality(ReleaseParser.getQualityKeyword(sub.Filename + " " + sub.Rip))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.Filename, FilenameUtils.isExtension(sub.Filename, "srt")))
                        .uploader(sub.Author)
                        .hearingImpaired(false))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
        // TODO Auto-generated method stub
        return null;
    }

}
