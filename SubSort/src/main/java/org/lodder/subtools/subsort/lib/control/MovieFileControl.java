package org.lodder.subtools.subsort.lib.control;

import java.util.List;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBAPI;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBException;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchID;
import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class MovieFileControl extends VideoFileControl {
  private final IMDBSearchID imdbSearchID;
  private final IMDBAPI imdbapi;

  public MovieFileControl(MovieRelease movieRelease, Manager manager) {
    super(movieRelease);
    imdbapi = new IMDBAPI(manager);
    imdbSearchID = new IMDBSearchID(manager);
  }

  @Override
  public Release process(List<MappingTvdbScene> dict) throws ReleaseControlException {
    Logger.instance.trace("MovieFileControl", "process", "");
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
            Logger.instance
                .error("Unable to get details from IMDB API, continue with filename info" + release);
          }

        } else {
          throw new ReleaseControlException("Movie not found on IMDB, check file", release);
        }
      } catch (IMDBException e) {
        Logger.instance.log(Logger.stack2String(e));
        throw new ReleaseControlException("IMDBAPI Failed", release);
      }
      return movieRelease;
    }
  }
}
