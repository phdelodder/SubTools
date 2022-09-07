package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.JAddic7edApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAddic7edAdapter implements JSubAdapter, SubtitleProvider {

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
    public String getName() {
        return "Addic7ed";
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
    public List<Subtitle> searchSubtitles(TvRelease release, String... sublanguageids) {
        Optional<String> serieName = Optional.empty();
        try {
            if (release.getShow().length() > 0) {
                serieName = jaapi.getAddictedSerieName(release.getShow());
            }
            if (serieName.isEmpty() && release.getOriginalShowName().length() > 0) {
                serieName = jaapi.getAddictedSerieName(release.getOriginalShowName());
            }
        } catch (ManagerSetupException e) {
            LOGGER.error("API JAddic7ed searchSubtitles using title ", e);
        }
        return serieName.map(name -> jaapi.searchSubtitles(name, release.getSeason(), release.getEpisodeNumbers().get(0), release.getTitle())
                .stream()
                .peek(sub -> {
                    switch (sub.getLanguage()) {
                        case "Dutch" -> sub.setLanguage("nl");
                        case "English" -> sub.setLanguage("en");
                        default -> {
                        }
                    }
                })
                .filter(sub -> sublanguageids[0].equals(sub.getLanguage()))

                .map(sub -> Subtitle.downloadSource(sub.getUrl())
                        .subtitleSource(Subtitle.SubtitleSource.ADDIC7ED)
                        .fileName(StringUtils.removeIllegalFilenameChars(sub.getTitel() + " " + sub.getVersion()))
                        .languageCode(sub.getLanguage())
                        .quality(ReleaseParser.getQualityKeyword(sub.getTitel() + " " + sub.getVersion()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getTitel() + " " + sub.getVersion(),
                                FilenameUtils.isExtension(sub.getTitel() + " " + sub.getVersion(), "srt")))
                        .uploader(sub.getUploader())
                        .hearingImpaired(false))
                .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
        // TODO Auto-generated method stub
        return null;
    }

}
