package org.lodder.subtools.subsort.lib.control;

import java.io.File;

import org.lodder.subtools.sublibrary.control.VideoFileParser;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.model.VideoType;

public class VideoFileControlFactory {

    private static final VideoFileParser videoFileParser = new VideoFileParser();

    public static VideoFileControl getController(File file, File basedir) throws VideoFileParseException, ControlFactoryException {
        VideoFile videoFile = videoFileParser.parse(file, basedir);
        if (videoFile.getVideoType() == VideoType.EPISODE) {
            return new EpisodeFileControl((EpisodeFile) videoFile);
        } else if (videoFile.getVideoType() == VideoType.MOVIE) {
            return new MovieFileControl((MovieFile) videoFile);
        }
        throw new ControlFactoryException("Can't find controller");
    }
}