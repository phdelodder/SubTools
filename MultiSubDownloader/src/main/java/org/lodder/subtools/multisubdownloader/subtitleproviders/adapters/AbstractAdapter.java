package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.lib.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <S> type of the subtitle objects returned by the api
 * @param <X> type of the exception thrown by the api
 */
public abstract class AbstractAdapter<S, X extends Exception> implements SubtitleProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdapter.class);

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language, UserInteractionHandler userInteraction) {
        Set<S> subtitles = new HashSet<>();
        if (StringUtils.isNotBlank(movieRelease.getFileName())) {
            File file = new File(movieRelease.getPath(), movieRelease.getFileName());
            if (file.exists()) {
                try {
                    searchMovieSubtitlesWithHash(OpenSubtitlesHasher.computeHash(file), language).forEach(subtitles::add);
                } catch (IOException e) {
                    LOGGER.error("Error calculating file hash", e);
                } catch (Exception e) {
                    LOGGER.error("API %s searchSubtitles using file hash for movie [%s] (%s)".formatted(getSubtitleSource().getName(),
                            movieRelease.getName(), e.getMessage()), e);
                }
            }
        }
        movieRelease.getImdbId().ifPresent(imdbId -> {
            try {
                searchMovieSubtitlesWithId(imdbId, language).forEach(subtitles::add);
            } catch (Exception e) {
                LOGGER.error("API %s searchSubtitles using imdbid [%s] for movie [%s] (%s)".formatted(getSubtitleSource().getName(),
                        imdbId, movieRelease.getName(), e.getMessage()), e);
            }
        });
        if (subtitles.isEmpty()) {
            try {
                searchMovieSubtitlesWithName(movieRelease.getName(), movieRelease.getYear(), language).forEach(subtitles::add);
            } catch (Exception e) {
                LOGGER.error("API %s searchSubtitles using title for movie [%s] (%s)".formatted(getSubtitleSource().getName(),
                        movieRelease.getName(), movieRelease.getName(), e.getMessage()), e);
            }
        }
        return convertToSubtitles(movieRelease, subtitles, language);
    }

    protected abstract Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<S> subtitles, Language language);

    protected abstract Collection<S> searchMovieSubtitlesWithHash(String hash, Language language) throws X;

    protected abstract Collection<S> searchMovieSubtitlesWithId(int tvdbId, Language language) throws X;

    protected abstract Collection<S> searchMovieSubtitlesWithName(String name, int year, Language language) throws X;

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language, UserInteractionHandler userInteraction) {
        Set<S> subtitles = new HashSet<>();
        if (StringUtils.isNotBlank(tvRelease.getOriginalName())) {
            tvRelease.getEpisodeNumbers()
                    .forEach(episode -> searchSerieSubtitlesForName(tvRelease.getOriginalName(), tvRelease.getSeason(), episode, language)
                            .forEach(subtitles::add));
        }
        if (subtitles.isEmpty() && StringUtils.isNotBlank(tvRelease.getName())
                && !StringUtils.equals(tvRelease.getOriginalName(), tvRelease.getName())) {
            tvRelease.getEpisodeNumbers()
                    .forEach(episode -> searchSerieSubtitlesForName(tvRelease.getName(), tvRelease.getSeason(), episode, language)
                            .forEach(subtitles::add));
        }
        return convertToSubtitles(tvRelease, subtitles, language);
    }

    private Collection<S> searchSerieSubtitlesForName(String name, int season, int episode, Language language) {
        try {
            return searchSerieSubtitles(name, season, episode, language);
        } catch (Exception e) {
            LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
                    TvRelease.formatName(name, season, episode), e.getMessage()), e);
            return List.of();

        }
    }

    protected abstract Collection<S> searchSerieSubtitles(String name, int season, int episode, Language language) throws X;

    protected abstract Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Set<S> subtitles, Language language);
}
