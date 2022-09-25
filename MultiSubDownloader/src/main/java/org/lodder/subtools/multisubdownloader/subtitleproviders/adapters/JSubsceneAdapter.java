package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.SubsceneApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSubsceneAdapter extends AbstractAdapter<SubsceneSubtitleDescriptor, SubsceneException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSubsceneAdapter.class);
    private static LazySupplier<SubsceneApi> api;

    public JSubsceneAdapter(Manager manager) {
        if (api == null) {
            api = new LazySupplier<>(() -> {
                try {
                    return new SubsceneApi(manager);
                } catch (Exception e) {
                    LOGGER.error("API Subscene INIT (%s)".formatted(e.getMessage()), e);
                }
                return null;
            });
        }
    }

    private SubsceneApi getApi() {
        return api.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.SUBSCENE;
    }

    @Override
    protected List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws SubsceneException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws SubsceneException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected List<SubsceneSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language) throws SubsceneException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<SubsceneSubtitleDescriptor> subtitles, Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    protected List<SubsceneSubtitleDescriptor> searchSerieSubtitles(String name, int season, int episode, Language language)
            throws SubsceneException {
        return getApi().getSubtilteDescriptors(name, season, language);
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Set<SubsceneSubtitleDescriptor> subtitles, Language language) {
        return subtitles.stream()
                .filter(sub -> language == sub.getLanguage())
                .filter(sub -> sub.getName().contains(getSeasonEpisodeString(tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))))
                .map(sub -> Subtitle.downloadSource(sub.getUrlSupplier())
                        .subtitleSource(getSubtitleSource())
                        .fileName(StringUtil.removeIllegalFilenameChars(sub.getName()))
                        .language(sub.getLanguage())
                        .quality(ReleaseParser.getQualityKeyword(sub.getName()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getName(), false))
                        .uploader(sub.getUploader())
                        .hearingImpaired(sub.isHearingImpaired()))
                .collect(Collectors.toSet());
    }
    private String getSeasonEpisodeString(int season, int episode) {
        return "S" + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(season), 2, "0") + "E"
                + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(episode), 2, "0");
    }
}
