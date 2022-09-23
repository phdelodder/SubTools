package org.lodder.subtools.multisubdownloader.lib.control;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBAPI;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBException;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchID;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchIDException;
import org.lodder.subtools.sublibrary.data.OMDB.OMDBAPI;
import org.lodder.subtools.sublibrary.data.OMDB.OMDBException;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class MovieReleaseControl extends ReleaseControl {
    private final IMDBSearchID imdbSearchID;
    private final IMDBAPI imdbapi;
    private final OMDBAPI omdbapi;
    private final MovieRelease movieRelease;

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieReleaseControl.class);

    public MovieReleaseControl(MovieRelease movieRelease, Settings settings, Manager manager) {
        super(settings, manager);
        this.movieRelease = movieRelease;
        imdbapi = new IMDBAPI(manager);
        omdbapi = new OMDBAPI(manager);
        imdbSearchID = new IMDBSearchID(manager);
    }

    @Override
    public void process(TvdbMappings tvdbMappings) throws ReleaseControlException {
        if ("".equals(movieRelease.getName())) {
            throw new ReleaseControlException("Unable to extract/find title, check file", movieRelease);
        } else {
            try {
                int imdbId = imdbSearchID.getImdbId(movieRelease.getName(), movieRelease.getYear())
                        .orElseThrow(() -> new ReleaseControlException("Movie not found on IMDB, check file", movieRelease));
                movieRelease.setImdbId(imdbId);
            } catch (IMDBSearchIDException e) {
                throw new ReleaseControlException("IMDBASearchID Failed", movieRelease);
            }
            try {
                imdbapi.getIMDBMovieDetails(movieRelease.getImdbidAsString()).ifPresentOrElse(imdbInfo -> {
                    movieRelease.setYear(imdbInfo.getYear());
                    movieRelease.setName(imdbInfo.getTitle());
                }, () -> LOGGER.error("Unable to get details from IMDB API, continue with filename info {}", movieRelease));
            } catch (IMDBException e) {
                LOGGER.warn("IMDBAPI Failed {}, using OMDBAPI as fallback", movieRelease);

                try {
                    omdbapi.getOMDBMovieDetails(movieRelease.getImdbidAsString()).ifPresentOrElse(omdbInfo -> {
                        movieRelease.setYear(omdbInfo.getYear());
                        movieRelease.setName(omdbInfo.getTitle());
                    }, () -> LOGGER.error("Unable to get details from OMDB API, continue with filename info {}", movieRelease));
                } catch (OMDBException e1) {
                    throw new ReleaseControlException("OMDBAPI Failed", movieRelease);
                }
            }
        }
    }

    @Override
    public Release getVideoFile() {
        return movieRelease;
    }

}
