package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtiltesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
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

public class JTVsubtitlesAdapter extends AbstractAdapter<TVsubtitlesSubtitleDescriptor, TvSubtiltesException> {

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
    protected List<TVsubtitlesSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws TvSubtiltesException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected List<TVsubtitlesSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws TvSubtiltesException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected List<TVsubtitlesSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language)
            throws TvSubtiltesException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<TVsubtitlesSubtitleDescriptor> subtitles, Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    protected Set<TVsubtitlesSubtitleDescriptor> searchSerieSubtitles(String name, int season, int episode, Language language)
            throws TvSubtiltesException {
        return jtvapi.searchSubtitles(name, season, episode, language);
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Set<TVsubtitlesSubtitleDescriptor> subtitles, Language language) {
        return subtitles.stream()
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
}
