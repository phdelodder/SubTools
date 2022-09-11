package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.JAddic7edApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAddic7edAdapter implements SubtitleProvider {

    private static JAddic7edApi jaapi;

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edAdapter.class);

    public JAddic7edAdapter(boolean isLoginEnabled, String username, String password, boolean speedy, Manager manager) {
        try {
            if (jaapi == null) {
                jaapi = isLoginEnabled ? new JAddic7edApi(username, password, speedy, manager) : new JAddic7edApi(speedy, manager);
            }
        } catch (Addic7edException e) {
            LOGGER.error("API JAddic7ed INIT: ", e);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease release, Language language) {
        Optional<String> serieName = Optional.empty();
        try {
            if (release.getShowName().length() > 0) {
                serieName = jaapi.getAddictedSerieName(release.getShowName());
            }
            if (serieName.isEmpty() && release.getOriginalShowName().length() > 0) {
                serieName = jaapi.getAddictedSerieName(release.getOriginalShowName());
            }
        } catch (ManagerSetupException e) {
            LOGGER.error("API JAddic7ed searchSubtitles using title ", e);
        }
        return serieName
                    .map(name -> jaapi.searchSubtitles(name, release.getSeason(), release.getEpisodeNumbers().get(0), release.getTitle(), language)
                        .stream()
                        .filter(sub -> language == sub.getLanguage())
                        .map(sub -> Subtitle.downloadSource(sub.getUrl())
                                .subtitleSource(getSubtitleSource())
                                .fileName(StringUtils.removeIllegalFilenameChars(sub.getTitel() + " " + sub.getVersion()))
                                .language(sub.getLanguage())
                                .quality(ReleaseParser.getQualityKeyword(sub.getTitel() + " " + sub.getVersion()))
                                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                                .releaseGroup(ReleaseParser.extractReleasegroup(sub.getTitel() + " " + sub.getVersion(),
                                        FilenameUtils.isExtension(sub.getTitel() + " " + sub.getVersion(), "srt")))
                                .uploader(sub.getUploader())
                                .hearingImpaired(false))
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        // TODO Auto-generated method stub
        return Set.of();
    }

}
