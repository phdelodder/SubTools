package org.lodder.subtools.subsort.lib.control;

import java.io.File;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class VideoFileControlFactory {

    private static final ReleaseParser releaseParser = new ReleaseParser();

    public static VideoFileControl getController(File file, Manager manager, UserInteractionHandler userInteractionHandler)
            throws ReleaseParseException, ControlFactoryException, ReleaseControlException {
        Release release = releaseParser.parse(file);
        if (release.getVideoType() == VideoType.EPISODE) {
            return new EpisodeFileControl((TvRelease) release);
        } else if (release.getVideoType() == VideoType.MOVIE) {
            return new MovieFileControl((MovieRelease) release, manager, userInteractionHandler);
        }
        throw new ControlFactoryException("Can't find controller");
    }
}
