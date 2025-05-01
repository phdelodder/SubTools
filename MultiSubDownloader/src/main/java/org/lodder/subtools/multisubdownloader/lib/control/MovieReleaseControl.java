package org.lodder.subtools.multisubdownloader.lib.control;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MovieReleaseControl extends ReleaseControl {
    private final ImdbAdapter imdbAdapter;
    private final OmdbAdapter omdbAdapter;
    private final MovieRelease movieRelease;

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieReleaseControl.class);

    public MovieReleaseControl(MovieRelease movieRelease, Settings settings, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(settings, manager);
        this.movieRelease = movieRelease;
        this.imdbAdapter = ImdbAdapter.getInstance(manager, userInteractionHandler);
        this.omdbAdapter = OmdbAdapter.getInstance(manager, userInteractionHandler);
    }

    @Override
    public void process() throws ReleaseControlException {
        if (StringUtils.isBlank(movieRelease.name)) {
            throw new ReleaseControlException("Unable to extract/find title, check file", movieRelease);
        } else {
            movieRelease.imdbId = imdbAdapter.getImdbId(movieRelease.name, movieRelease.year)
                .orElseThrow(() -> new ReleaseControlException("Movie not found on IMDB, check file", movieRelease));

            ReleaseDBIntf movieDetails = imdbAdapter.getMovieDetails(movieRelease.imdbId).orElse(null);
            if (movieDetails == null) {
                movieDetails = omdbAdapter.getMovieDetails(movieRelease.imdbId).orElse(null);
            }
            if (movieDetails != null) {
                movieRelease.year = movieDetails.year;
                movieRelease.name = movieDetails.name;
            } else {
                LOGGER.error("Unable to get details from OMDB API, continue with filename info $movieRelease");
            }
        }
    }

    @Override
    public Release getVideoFile() {
        return movieRelease;
    }
}
