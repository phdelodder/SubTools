package org.lodder.subtools.subsort.lib.control;

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
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class MovieFileControl extends VideoFileControl {
    private final IMDBSearchID imdbSearchID;
    private final IMDBAPI imdbapi;
    private final OMDBAPI omdbapi;

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieFileControl.class);

    public MovieFileControl(MovieRelease movieRelease, Manager manager) throws ReleaseControlException {
        super(movieRelease);
        imdbapi = new IMDBAPI(manager);
        omdbapi = new OMDBAPI(manager);
        imdbSearchID = new IMDBSearchID(manager);
        process(movieRelease);
    }

    private Release process(MovieRelease movieRelease) throws ReleaseControlException {
        if ("".equals(movieRelease.getName())) {
            throw new ReleaseControlException("Unable to extract/find title, check file", release);
        } else {
            try {
                int imdbId = imdbSearchID.getImdbId(movieRelease.getName(), movieRelease.getYear())
                        .orElseThrow(() -> new ReleaseControlException("Movie not found on IMDB, check file", release));
                movieRelease.setImdbId(imdbId);
            } catch (IMDBSearchIDException e) {
                throw new ReleaseControlException("IMDBASearchID Failed", release);
            }
            try {
                imdbapi.getIMDBMovieDetails(movieRelease.getImdbidAsString()).ifPresentOrElse(imdbinfo -> {
                    movieRelease.setYear(imdbinfo.getYear());
                    movieRelease.setName(imdbinfo.getTitle());
                }, () -> LOGGER.error("Unable to get details from IMDB API, continue with filename info {}", release));
            } catch (IMDBException e) {
                LOGGER.error("OMDBAPI Failed {}, using OMDBAPI as fallback", release);

                try {
                    omdbapi.getOMDBMovieDetails(movieRelease.getImdbidAsString()).ifPresentOrElse(omdbinfo -> {
                        movieRelease.setYear(omdbinfo.getYear());
                        movieRelease.setName(omdbinfo.getTitle());
                    }, () -> LOGGER.error("Unable to get details from OMDB API, continue with filename info {}", release));
                } catch (OMDBException e1) {
                    throw new ReleaseControlException("OMDBAPI Failed", release);
                }
            }
            return movieRelease;
        }
    }
}
