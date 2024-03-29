package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.lodder.subtools.multisubdownloader.lib.control.MovieReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.TvReleaseControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ FileUtils.class, Files.class })
public class Local implements SubtitleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Local.class);

    private final Settings settings;
    @Getter
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public Local(Settings settings, Manager manager, UserInteractionHandler userInteractionHandler) {
        this.settings = settings;
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.LOCAL;
    }

    private List<Path> getPossibleSubtitles(String filter) {
        return settings.getLocalSourcesFolders().stream()
                .flatMap(local -> getAllSubtitlesFiles(local, filter).stream())
                .toList();
    }

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {
        Set<Subtitle> listFoundSubtitles = new HashSet<>();
        ReleaseParser vfp = new ReleaseParser();

        String filter;
        if (tvRelease.getOriginalName().length() > 0) {
            filter = tvRelease.getOriginalName().replaceAll("[^A-Za-z]", "").trim();
        } else {
            filter = tvRelease.getName().replaceAll("[^A-Za-z]", "").trim();
        }

        for (Path fileSub : getPossibleSubtitles(filter)) {
            try {
                Release release = vfp.parse(fileSub);
                if ((release.getVideoType() == VideoType.EPISODE)
                        && (((TvRelease) release).getSeason() == tvRelease.getSeason() && Utils.containsAll(
                                ((TvRelease) release).getEpisodeNumbers(), tvRelease.getEpisodeNumbers()))) {

                    TvReleaseControl epCtrl = new TvReleaseControl((TvRelease) release, settings, manager, userInteractionHandler);
                    epCtrl.process();
                    if (((TvRelease) release).getTvdbId().equals(tvRelease.getTvdbId())) {
                        Language detectedLang = DetectLanguage.execute(fileSub);
                        if (detectedLang == language) {
                            LOGGER.debug("Local Sub found, adding [{}]", fileSub);
                            listFoundSubtitles.add(
                                    Subtitle.downloadSource(fileSub)
                                            .subtitleSource(getSubtitleSource())
                                            .fileName(fileSub.getFileNameAsString())
                                            .language(language)
                                            .quality(ReleaseParser.getQualityKeyword(fileSub.getFileNameAsString()))
                                            .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                                            .releaseGroup(ReleaseParser.extractReleasegroup(fileSub.getFileNameAsString(), true))
                                            .uploader(fileSub.toAbsolutePath().toString())
                                            .hearingImpaired(false));
                        }
                    }
                }
            } catch (ReleaseParseException | ReleaseControlException e) {
                if (LOGGER.isDebugEnabled() || LOGGER.isTraceEnabled()) {
                    LOGGER.error(e.getMessage(), e);
                } else {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        return listFoundSubtitles;
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        Set<Subtitle> listFoundSubtitles = new HashSet<>();
        ReleaseParser releaseParser = new ReleaseParser();

        String filter = movieRelease.getName();

        for (Path fileSub : getPossibleSubtitles(filter)) {
            try {
                Release release = releaseParser.parse(fileSub);
                if (release.getVideoType() == VideoType.MOVIE) {
                    MovieReleaseControl movieCtrl = new MovieReleaseControl((MovieRelease) release, settings, manager, userInteractionHandler);
                    movieCtrl.process();
                    if (((MovieRelease) release).getImdbId().equals(movieRelease.getImdbId())) {
                        Language detectedLang = DetectLanguage.execute(fileSub);
                        if (detectedLang == language) {
                            LOGGER.debug("Local Sub found, adding {}", fileSub);
                            listFoundSubtitles.add(
                                    Subtitle.downloadSource(fileSub)
                                            .subtitleSource(getSubtitleSource())
                                            .fileName(fileSub.getFileNameAsString())
                                            .language(language) // TODO previously: language(""). This was not correct?
                                            .quality(ReleaseParser.getQualityKeyword(fileSub.getFileNameAsString()))
                                            .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                                            .releaseGroup(ReleaseParser.extractReleasegroup(fileSub.getFileNameAsString(), true))
                                            .uploader(fileSub.toAbsolutePath().toString())
                                            .hearingImpaired(false));
                        }
                    }
                }
            } catch (ReleaseParseException | ReleaseControlException e) {
                if (LOGGER.isDebugEnabled() || LOGGER.isTraceEnabled()) {
                    LOGGER.error(e.getMessage(), e);
                } else {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        return listFoundSubtitles;
    }

    private List<Path> getAllSubtitlesFiles(Path dir, String filter) {
        try {
            return dir.list().filter(Files::isRegularFile)
                    .filter(file -> file.hasExtension("srt"))
                    .filter(file -> file.getFileNameAsString().replaceAll("[^A-Za-z]", "").toLowerCase().contains(filter.toLowerCase()))
                    .toList();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public String getProviderName() {
        return getSubtitleSource().name();
    }

    @Override
    public <X extends Exception> Optional<SerieMapping> getProviderSerieId(TvRelease tvRelease) throws X {
        throw new NotImplementedException();
    }
}
