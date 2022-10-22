package org.lodder.subtools.subsort.lib.control;

import java.io.File;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class VideoFileFactory {

    public static Release get(final File file, Manager manager, UserInteractionHandler userInteractionHandler)
            throws ControlFactoryException, ReleaseParseException, ReleaseControlException {
        return VideoFileControlFactory.getController(file, manager, userInteractionHandler).getVideoFile();
    }

}
