package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBAPI;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBException;
import org.lodder.subtools.sublibrary.data.IMDB.IMDBSearchID;
import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class MovieFileControl extends VideoFileControl {
  private final IMDBSearchID imdbSearchID;
  private final IMDBAPI imdbapi;

  public MovieFileControl(MovieFile movieFile, Settings settings) {
    super(movieFile, settings);
    imdbapi = new IMDBAPI();
    imdbSearchID = new IMDBSearchID();
  }

  @Override
  public void process(List<MappingTvdbScene> dict) throws VideoControlException {
    Logger.instance.trace("MovieFileControl", "process", "");
    MovieFile movieFile = (MovieFile) release;
    if (movieFile.getTitle().equals("")) {
      throw new VideoControlException("Unable to extract/find title, check file", release);
    } else {
      int imdbid;
      imdbid = imdbSearchID.getImdbId(movieFile.getTitle(), movieFile.getYear());
      if (imdbid > 0) {
        movieFile.setImdbid(imdbid);
        IMDBDetails imdbinfo;
        try {
          imdbinfo = imdbapi.getIMDBMovieDetails(movieFile.getImdbidAsString());
          if (imdbinfo != null) {
            movieFile.setYear(imdbinfo.getYear());
            movieFile.setTitle(imdbinfo.getTitle());
          } else {
            Logger.instance
                .error("Unable to get details from IMDB API, continue with filename info"
                    + release);
          }
        } catch (IMDBException e) {
          throw new VideoControlException("IMDBAPI Failed", release);
        }

      } else {
        throw new VideoControlException("Movie not found on IMDB, check file", release);
      }
    }
  }

  @Override
  public void processWithSubtitles(List<MappingTvdbScene> dict, String languageCode)
      throws VideoControlException {
    Logger.instance.trace("MovieFileControl", "processWithSubtitles", "");
    process(dict);
    release.setMatchingSubs(sc.getSubtitles((MovieFile) release, languageCode));
  }

}
