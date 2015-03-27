package org.lodder.subtools.subsort.lib.control;

import java.io.File;
import java.util.List;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class VideoFileFactory {

  public static Release get(final File file, List<MappingTvdbScene> dict, Manager manager) throws ControlFactoryException, ReleaseParseException, ReleaseControlException {
    VideoFileControl vfc = VideoFileControlFactory.getController(file, manager);
    Release release = null;
		if (vfc instanceof EpisodeFileControl) {
			release = vfc.process(dict);
		} else if (vfc instanceof MovieFileControl) {
			release = vfc.process(dict);
		}
		return release;
	}

}
