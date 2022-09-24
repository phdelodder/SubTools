package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtiltesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
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
    public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {
        Set<TVsubtitlesSubtitleDescriptor> lSubtitles = new HashSet<>();
        String showName = StringUtils.isNotBlank(tvRelease.getOriginalShowName()) ? tvRelease.getOriginalShowName() : tvRelease.getName();

        if (StringUtils.isNotBlank(showName)) {
            if (showName.contains("(") && showName.contains(")")) {
                String alterName = showName.substring(0, showName.indexOf("(") - 1).trim();
                try {
                    lSubtitles = jtvapi.searchSubtitles(alterName, tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0), language);
                } catch (TvSubtiltesException e) {
                    LOGGER.error("API TVSubtitles searchSubtitles using name for serie [%s] (%s)".formatted(
                            TvRelease.formatName(alterName, tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0)), e.getMessage()), e);
                }
            }
            try {
                lSubtitles.addAll(jtvapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0), language));
            } catch (TvSubtiltesException e) {
                LOGGER.error("API TVSubtitles searchSubtitles using name for serie [%s] (%s)".formatted(
                        TvRelease.formatName(showName, tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0)), e.getMessage()), e);
            }
        }
        return lSubtitles.stream()
                .map(sub -> Subtitle.downloadSource(sub.getUrl())
                        .subtitleSource(getSubtitleSource())
                        .fileName(sub.getFilename())
                        .language(language)
                        .quality(ReleaseParser.getQualityKeyword(sub.getFilename() + " " + sub.getRip()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getFilename(), FilenameUtils.isExtension(sub.getFilename(), "srt")))
                        .uploader(sub.getAuthor())
                        .hearingImpaired(false))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        // TODO Auto-generated method stub
        return Set.of();
    }

}
