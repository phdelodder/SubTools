package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
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
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPodnapisiAdapter extends AbstractAdapter<PodnapisiSubtitleDescriptor, PodnapisiException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiAdapter.class);
    private static LazySupplier<JPodnapisiApi> jpapi;

    public JPodnapisiAdapter(Manager manager) {
        if (jpapi == null) {
            jpapi = new LazySupplier<>(() -> {
                try {
                    return new JPodnapisiApi("JBierSubDownloader", manager);
                } catch (Exception e) {
                    LOGGER.error("API Podnapisi INIT (%s)".formatted(e.getMessage()), e);
                }
                return null;
            });
        }
    }

    private JPodnapisiApi getApi() {
        return jpapi.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.PODNAPISI;
    }


    @Override
    protected List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws PodnapisiException {
        return getApi().searchSubtitles(new String[] { hash }, language);
    }

    @Override
    protected List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws PodnapisiException {
        return List.of();
    }

    @Override
    protected List<PodnapisiSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language) throws PodnapisiException {
        return getApi().searchSubtitles(name, year, 0, 0, language);
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<PodnapisiSubtitleDescriptor> subtitles, Language language) {
        return buildListSubtitles(language, subtitles);
    }


    @Override
    protected List<PodnapisiSubtitleDescriptor> searchSerieSubtitles(String name, int season, int episode, Language language,
            UserInteractionHandler userInteractionHandler) throws PodnapisiException {
        return getApi().searchSubtitles(name, 0, season, episode, language);
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Set<PodnapisiSubtitleDescriptor> subtitles, Language language) {
        return buildListSubtitles(language, subtitles);
    }

    private Set<Subtitle> buildListSubtitles(Language language, Set<PodnapisiSubtitleDescriptor> lSubtitles) {
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
