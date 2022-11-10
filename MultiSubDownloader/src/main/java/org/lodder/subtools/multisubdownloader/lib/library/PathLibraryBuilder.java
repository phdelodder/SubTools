package org.lodder.subtools.multisubdownloader.lib.library;

import java.io.File;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.StringUtil;

public class PathLibraryBuilder extends LibraryBuilder {

    public PathLibraryBuilder(LibrarySettings librarySettings, Manager manager, UserInteractionHandler userInteractionHandler) {
        super(librarySettings, manager, userInteractionHandler);
    }

    @Override
    public String build(Release release) {
        if (LibraryActionType.MOVE.equals(getLibrarySettings().getLibraryAction())
                || LibraryActionType.MOVEANDRENAME.equals(getLibrarySettings().getLibraryAction())) {
            String folder = "";
            if (release instanceof TvRelease tvRelease) {
                folder = buildEpisode(tvRelease);
            } else if (release instanceof MovieRelease movieRelease) {
                folder = buildMovie(movieRelease);
            }
            return new File(getLibrarySettings().getLibraryFolder(), folder).toString();
        } else {
            return release.getPath().toString();
        }
    }

    protected String buildEpisode(TvRelease tvRelease) {
        String folder = getLibrarySettings().getLibraryFolderStructure();
        String show = getShowName(tvRelease.getName());
        if (getLibrarySettings().isLibraryReplaceChars()) {
            show = StringUtil.removeIllegalWindowsChars(show);
        }

        folder = folder.replace("%SHOW NAME%", show);
        // order is important!
        folder = replaceFormatedEpisodeNumber(folder, "%EEX%", tvRelease.getEpisodeNumbers(), true);
        folder = replaceFormatedEpisodeNumber(folder, "%EX%", tvRelease.getEpisodeNumbers(), false);
        folder = folder.replace("%SS%", formatedNumber(tvRelease.getSeason(), true));
        folder = folder.replace("%S%", formatedNumber(tvRelease.getSeason(), false));
        folder = folder.replace("%EE%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), true));
        folder = folder.replace("%E%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), false));
        folder = folder.replace("%TITLE%", tvRelease.getTitle());
        try {
            folder = folder.replace("%SEPARATOR%", File.separator);
        } catch (IndexOutOfBoundsException | IllegalArgumentException ioobe) {
            // windows hack needs "\\" instead of "\"
            folder = folder.replace("%SEPARATOR%", File.separator + File.separator);
        }
        folder = folder.replace("%QUALITY%", tvRelease.getQuality());

        if (getLibrarySettings().isLibraryFolderReplaceSpace()) {
            folder = folder.replace(" ", getLibrarySettings().getLibraryFolderReplacingSpaceSign());
        }

        return folder;
    }

    protected String buildMovie(MovieRelease movieRelease) {
        String folder = getLibrarySettings().getLibraryFolderStructure();
        String title = movieRelease.getName();

        if (getLibrarySettings().isLibraryReplaceChars()) {
            title = StringUtil.removeIllegalWindowsChars(title);
        }

        folder = folder.replace("%MOVIE TITLE%", title);
        folder = folder.replace("%YEAR%", Integer.toString(movieRelease.getYear()));
        folder = folder.replace("%SEPARATOR%", File.separator);
        folder = folder.replace("%QUALITY%", movieRelease.getQuality());

        if (getLibrarySettings().isLibraryFolderReplaceSpace()) {
            folder = folder.replace(" ", getLibrarySettings().getLibraryFolderReplacingSpaceSign());
        }

        return folder;
    }

}
