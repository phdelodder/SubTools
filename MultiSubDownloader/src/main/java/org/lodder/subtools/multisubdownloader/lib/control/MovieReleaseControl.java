package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBAPI;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBException;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchID;
import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieReleaseControl extends ReleaseControl {
  private final IMDBSearchID imdbSearchID;
  private final IMDBAPI imdbapi;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MovieReleaseControl.class);

  public MovieReleaseControl(MovieRelease movieRelease, Settings settings, Manager manager) {
    super(movieRelease, settings, manager);
    imdbapi = new IMDBAPI(manager);
    imdbSearchID = new IMDBSearchID(manager);
  }

  @Override
  public void process(List<MappingTvdbScene> dict) throws ReleaseControlException {
    MovieRelease movieRelease = (MovieRelease) release;
    if (movieRelease.getTitle().equals("")) {
      throw new ReleaseControlException("Unable to extract/find title, check file", release);
    } else {
      int imdbid = -1;
      try {
        imdbid = imdbSearchID.getImdbId(movieRelease.getTitle(), movieRelease.getYear());
        if (imdbid > 0) {
          movieRelease.setImdbid(imdbid);
          IMDBDetails imdbinfo;

          imdbinfo = imdbapi.getIMDBMovieDetails(movieRelease.getImdbidAsString());
          if (imdbinfo != null) {
            movieRelease.setYear(imdbinfo.getYear());
            movieRelease.setTitle(imdbinfo.getTitle());
          } else {
            LOGGER.error("Unable to get details from IMDB API, continue with filename info {}", release);
          }
        } else {
          throw new ReleaseControlException("Movie not found on IMDB, check file", release);
        }
      } catch (IMDBException e) {
        throw new ReleaseControlException("IMDBAPI Failed", release);
      }
    }
  }

}
