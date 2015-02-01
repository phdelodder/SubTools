package org.lodder.subtools.subsort.lib.control;

import java.io.File;
import java.util.List;

import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class VideoFileFactory {

	public static Release get(final File file, final File basedir, List<MappingTvdbScene> dict) throws ControlFactoryException,
			ReleaseParseException, ReleaseControlException {
		Logger.instance.trace("VideoFileFactory", "get", "");
		VideoFileControl vfc = VideoFileControlFactory.getController(file,
				basedir);
		Release release = null;
		if (vfc instanceof EpisodeFileControl) {
			release = vfc.process(dict);
		} else if (vfc instanceof MovieFileControl) {
			release = vfc.process(dict);
		}
		return release;
	}

}
