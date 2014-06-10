package org.lodder.subtools.sublibrary.exception;

import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class VideoControlException extends Exception {

    public VideoControlException(String string, VideoFile videoFile) {
        super(string + ": " + videoFile.getFilename());
        Logger.instance.trace("VideoControlException", "VideoControlException", "");
    }

    /**
     *
     */
    private static final long serialVersionUID = 1958337660409078923L;

}
