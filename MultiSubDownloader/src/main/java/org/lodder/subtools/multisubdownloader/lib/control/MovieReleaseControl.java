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
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class MovieReleaseControl extends ReleaseControl<MovieRelease> {
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
        if ("".equals(getRelease().getName())) {
            throw new ReleaseControlException("Unable to extract/find title, check file", getRelease());
        } else {
            try {
                int imdbId = imdbSearchID.getImdbId(getRelease().getName(), getRelease().getYear())
                        .orElseThrow(() -> new ReleaseControlException("Movie not found on IMDB, check file", getRelease()));
                getRelease().setImdbId(imdbId);
            } catch (IMDBSearchIDException e) {
                throw new ReleaseControlException("IMDBASearchID Failed", getRelease());
            }
            try {
                imdbapi.getIMDBMovieDetails(getRelease().getImdbidAsString()).ifPresentOrElse(imdbInfo -> {
                    getRelease().setYear(imdbInfo.getYear());
                    getRelease().setName(imdbInfo.getTitle());
                }, () -> LOGGER.error("Unable to get details from IMDB API, continue with filename info {}", getRelease()));
            } catch (IMDBException e) {
                LOGGER.warn("IMDBAPI Failed {}, using OMDBAPI as fallback", getRelease());

                try {
                    omdbapi.getOMDBMovieDetails(getRelease().getImdbidAsString()).ifPresentOrElse(omdbInfo -> {
                        getRelease().setYear(omdbInfo.getYear());
                        getRelease().setName(omdbInfo.getTitle());
                    }, () -> LOGGER.error("Unable to get details from OMDB API, continue with filename info {}", getRelease()));
                } catch (OMDBException e1) {
                    throw new ReleaseControlException("OMDBAPI Failed", getRelease());
                }
            }
        }
    }

}
