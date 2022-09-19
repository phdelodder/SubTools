package org.lodder.subtools.multisubdownloader.lib.control;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBAPI;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBException;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchID;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchIDException;
import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.lodder.subtools.sublibrary.data.OMDB.OMDBAPI;
import org.lodder.subtools.sublibrary.data.OMDB.OMDBException;
import org.lodder.subtools.sublibrary.data.OMDB.model.OMDBDetails;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieReleaseControl extends ReleaseControl {
    private final IMDBSearchID imdbSearchID;
    private final IMDBAPI imdbapi;
    private final OMDBAPI omdbapi;

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieReleaseControl.class);

    public MovieReleaseControl(MovieRelease movieRelease, Settings settings, Manager manager) {
        super(movieRelease, settings, manager);
        imdbapi = new IMDBAPI(manager);
        omdbapi = new OMDBAPI(manager);
        imdbSearchID = new IMDBSearchID(manager);
    }

    @Override
    public void process(TvdbMappings tvdbMappings) throws ReleaseControlException {
        MovieRelease movieRelease = (MovieRelease) release;
        if ("".equals(movieRelease.getName())) {
            throw new ReleaseControlException("Unable to extract/find title, check file", release);
        } else {
            int imdbid;
            try {
                imdbid = imdbSearchID.getImdbId(movieRelease.getName(), movieRelease.getYear());
            } catch (IMDBSearchIDException e) {
                throw new ReleaseControlException("IMDBASearchID Failed", release);
            }
            try {
                if (imdbid > 0) {
                    movieRelease.setImdbId(imdbid);
                    IMDBDetails imdbinfo = imdbapi.getIMDBMovieDetails(movieRelease.getImdbidAsString());
                    if (imdbinfo != null) {
                        movieRelease.setYear(imdbinfo.getYear());
                        movieRelease.setName(imdbinfo.getTitle());
                    } else {
                        LOGGER.error("Unable to get details from IMDB API, continue with filename info {}", release);
                    }
                } else {
                    throw new ReleaseControlException("Movie not found on IMDB, check file", release);
                }
            } catch (IMDBException e) {
                LOGGER.warn("IMDBAPI Failed {}, using OMDBAPI as fallback", release);

                try {
                    OMDBDetails omdbinfo = omdbapi.getOMDBMovieDetails(movieRelease.getImdbidAsString());
                    if (omdbinfo != null) {
                        movieRelease.setYear(omdbinfo.getYear());
                        movieRelease.setName(omdbinfo.getTitle());
                    } else {
                        LOGGER.error("Unable to get details from OMDB API, continue with filename info {}", release);
                    }
                } catch (OMDBException e1) {
                    throw new ReleaseControlException("OMDBAPI Failed", release);
                }
            }
        }
    }

}
