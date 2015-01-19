package org.lodder.subtools.subsort.lib.control;

import java.io.File;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;

public class VideoFileControlFactory {

    private static final ReleaseParser releaseParser = new ReleaseParser();

    public static VideoFileControl getController(File file, File basedir) throws VideoFileParseException, ControlFactoryException {
        Release release = releaseParser.parse(file, basedir);
        if (release.getVideoType() == VideoType.EPISODE) {
            return new EpisodeFileControl((EpisodeFile) release);
        } else if (release.getVideoType() == VideoType.MOVIE) {
            return new MovieFileControl((MovieFile) release);
        }
        throw new ControlFactoryException("Can't find controller");
    }
}