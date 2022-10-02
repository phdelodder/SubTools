package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.JAddic7edApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edAdapter extends AbstractAdapter<Addic7edSubtitleDescriptor, Addic7edException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edAdapter.class);
    private static LazySupplier<JAddic7edApi> jaapi;

    public JAddic7edAdapter(boolean isLoginEnabled, String username, String password, boolean speedy, Manager manager,
            boolean confirmProviderMapping) {
        if (jaapi == null) {
            jaapi = new LazySupplier<>(() -> {
                try {
                    return isLoginEnabled ? new JAddic7edApi(username, password, speedy, manager, confirmProviderMapping)
                            : new JAddic7edApi(speedy, manager, confirmProviderMapping);
                } catch (Exception e) {
                    LOGGER.error("API Addic7ed INIT (%s)".formatted(e.getMessage()), e);
                }
                return null;
            });
        }
    }

    private JAddic7edApi getApi() {
        return jaapi.get();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }

    @Override
    protected List<Addic7edSubtitleDescriptor> searchMovieSubtitlesWithHash(String hash, Language language) throws Addic7edException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected List<Addic7edSubtitleDescriptor> searchMovieSubtitlesWithId(int tvdbId, Language language) throws Addic7edException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected List<Addic7edSubtitleDescriptor> searchMovieSubtitlesWithName(String name, int year, Language language)
            throws Addic7edException {
        // TODO implement this
        return List.of();
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<Addic7edSubtitleDescriptor> subtitles, Language language) {
        // TODO implement this
        return Set.of();
    }

    @Override
    protected List<Addic7edSubtitleDescriptor> searchSerieSubtitles(String name, int season, int episode, Language language,
            UserInteractionHandler userInteractionHandler) throws Addic7edException {
        return getApi().getAddictedSerieId(name, userInteractionHandler)
                .mapToObj(addic7edName -> getApi().searchSubtitles(addic7edName, season, episode, language))
                .orElseGet(List::of);
    }

    @Override
    protected Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Set<Addic7edSubtitleDescriptor> subtitles, Language language) {
        return subtitles.stream()
                .filter(sub -> language == sub.getLanguage())
                .map(sub -> Subtitle.downloadSource(sub.getUrl())
                        .subtitleSource(getSubtitleSource())
                        .fileName(StringUtil.removeIllegalFilenameChars(sub.getTitel() + " " + sub.getVersion()))
                        .language(sub.getLanguage())
                        .quality(ReleaseParser.getQualityKeyword(sub.getTitel() + " " + sub.getVersion()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getTitel() + " " + sub.getVersion(),
                                FilenameUtils.isExtension(sub.getTitel() + " " + sub.getVersion(), "srt")))
                        .uploader(sub.getUploader())
                        .hearingImpaired(false))
                .collect(Collectors.toSet());
    }
}
