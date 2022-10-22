package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;
import org.lodder.subtools.sublibrary.data.imdb.ImdbAdapter;
import org.lodder.subtools.sublibrary.data.omdb.OmdbAdapter;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class MovieReleaseControl extends ReleaseControl {
    private final ImdbAdapter imdbAdapter;
    private final OmdbAdapter omdbAdapter;
    private final MovieRelease movieRelease;

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieReleaseControl.class);

    public MovieReleaseControl(MovieRelease movieRelease, Settings settings, Manager manager, UserInteractionHandler userInteractionHandler) {
        super(settings, manager);
        this.movieRelease = movieRelease;
        this.imdbAdapter = ImdbAdapter.getInstance(manager, userInteractionHandler);
        this.omdbAdapter = OmdbAdapter.getInstance(manager, userInteractionHandler);
    }

    @Override
    public void process() throws ReleaseControlException {
        if (StringUtils.isBlank(movieRelease.getName())) {
            throw new ReleaseControlException("Unable to extract/find title, check file", movieRelease);
        } else {
            int imdbId = imdbAdapter.getImdbId(movieRelease.getName(), movieRelease.getYear())
                    .orElseThrow(() -> new ReleaseControlException("Movie not found on IMDB, check file", movieRelease));
            movieRelease.setImdbId(imdbId);

            Optional<? extends ReleaseDBIntf> movieDetails =
                    movieRelease.getImdbId().mapToObj(imdbAdapter::getMovieDetails).orElseGet(Optional::empty);
            if (movieDetails.isEmpty()) {
                movieDetails = movieRelease.getImdbId().mapToObj(omdbAdapter::getMovieDetails).orElseGet(Optional::empty);
            }
            movieDetails.ifPresentDo(info -> {
                movieRelease.setYear(info.getYear());
                movieRelease.setName(info.getName());
            }).ifEmptyDo(() -> LOGGER.error("Unable to get details from OMDB API, continue with filename info {}", movieRelease));
        }
    }

    @Override
    public Release getVideoFile() {
        return movieRelease;
    }
}
