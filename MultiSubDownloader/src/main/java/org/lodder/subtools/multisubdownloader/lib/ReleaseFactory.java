package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.control.MovieReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.ReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.TvReleaseControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseFactory {

    private final ReleaseParser releaseParser;
    private ReleaseControl releaseControl;
    private final Settings settings;
    private final Manager manager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseFactory.class);

    public ReleaseFactory(Settings settings, Manager manager) {
        releaseParser = new ReleaseParser();
        this.settings = settings;
        this.manager = manager;
    }

    public Release createRelease(final File file, UserInteractionHandler userInteractionHandler) {
        Release r = null;

        try {
            r = releaseParser.parse(file);

            releaseControl = switch (r.getVideoType()) {
                case EPISODE -> new TvReleaseControl((TvRelease) r, settings, manager, userInteractionHandler);
                case MOVIE -> new MovieReleaseControl((MovieRelease) r, settings, manager, userInteractionHandler);
                default -> releaseControl;
            };
            releaseControl.process();
            r = releaseControl.getVideoFile();

        } catch (ReleaseParseException | ReleaseControlException e) {
            LOGGER.error("createRelease", e);
        }
        return r;
    }
}
