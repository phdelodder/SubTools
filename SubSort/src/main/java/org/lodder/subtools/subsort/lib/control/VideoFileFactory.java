package org.lodder.subtools.subsort.lib.control;

import java.io.File;
import java.util.List;

import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class VideoFileFactory {

	public static VideoFile get(final File file, final File basedir, List<MappingTvdbScene> dict) throws ControlFactoryException,
			VideoFileParseException, VideoControlException {
		Logger.instance.trace("VideoFileFactory", "get", "");
		VideoFileControl vfc = VideoFileControlFactory.getController(file,
				basedir);
		VideoFile videoFile = null;
		if (vfc instanceof EpisodeFileControl) {
			videoFile = vfc.process(dict);
		} else if (vfc instanceof MovieFileControl) {
			videoFile = vfc.process(dict);
		}
		return videoFile;
	}

}
