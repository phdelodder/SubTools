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

        folder = folder.replaceAll("%SHOW NAME%", show);
        // order is important!
        folder = replaceFormatedEpisodeNumber(folder, "%EEX%", tvRelease.getEpisodeNumbers(), true);
        folder = replaceFormatedEpisodeNumber(folder, "%EX%", tvRelease.getEpisodeNumbers(), false);
        folder = folder.replaceAll("%SS%", formatedNumber(tvRelease.getSeason(), true));
        folder = folder.replaceAll("%S%", formatedNumber(tvRelease.getSeason(), false));
        folder = folder.replaceAll("%EE%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), true));
        folder = folder.replaceAll("%E%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), false));
        folder = folder.replaceAll("%TITLE%", tvRelease.getTitle());
        try {
            folder = folder.replaceAll("%SEPARATOR%", File.separator);
        } catch (IndexOutOfBoundsException | IllegalArgumentException ioobe) {
            // windows hack needs "\\" instead of "\"
            folder = folder.replaceAll("%SEPARATOR%", File.separator + File.separator);
        }
        folder = folder.replaceAll("%QUALITY%", tvRelease.getQuality());

        if (getLibrarySettings().isLibraryFolderReplaceSpace()) {
            folder = folder.replaceAll(" ", getLibrarySettings().getLibraryFolderReplacingSpaceSign());
        }

        return folder;
    }

    protected String buildMovie(MovieRelease movieRelease) {
        String folder = getLibrarySettings().getLibraryFolderStructure();
        String title = movieRelease.getName();

        if (getLibrarySettings().isLibraryReplaceChars()) {
            title = StringUtil.removeIllegalWindowsChars(title);
        }

        folder = folder.replaceAll("%MOVIE TITLE%", title);
        folder = folder.replaceAll("%YEAR%", Integer.toString(movieRelease.getYear()));
        folder = folder.replaceAll("%SEPARATOR%", File.separator);
        folder = folder.replaceAll("%QUALITY%", movieRelease.getQuality());

        if (getLibrarySettings().isLibraryFolderReplaceSpace()) {
            folder = folder.replaceAll(" ", getLibrarySettings().getLibraryFolderReplacingSpaceSign());
        }

        return folder;
    }

}
