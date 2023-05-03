package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    public Path build(Release release) {
        if (getLibrarySettings().hasAnyLibraryAction(LibraryActionType.MOVE, LibraryActionType.MOVEANDRENAME)) {
            Path subpath;
            if (release instanceof TvRelease tvRelease) {
                subpath = buildEpisode(tvRelease);
            } else if (release instanceof MovieRelease movieRelease) {
                subpath = buildMovie(movieRelease);
            } else {
                subpath = Path.of("");
            }
            return getLibrarySettings().getLibraryFolder().resolve(subpath);
        } else {
            return release.getPath();
        }
    }

    protected Path buildEpisode(TvRelease tvRelease) {
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
        folder = folder.replace("%QUALITY%", tvRelease.getQuality());
        if (getLibrarySettings().isLibraryFolderReplaceSpace()) {
            folder = folder.replace(" ", getLibrarySettings().getLibraryFolderReplacingSpaceSign());
        }
        return Paths.get("", folder.split("%SEPARATOR%"));
    }

    protected Path buildMovie(MovieRelease movieRelease) {
        String folder = getLibrarySettings().getLibraryFolderStructure();
        String title = movieRelease.getName();

        if (getLibrarySettings().isLibraryReplaceChars()) {
            title = StringUtil.removeIllegalWindowsChars(title);
        }

        folder = folder.replace("%MOVIE TITLE%", title);
        folder = folder.replace("%YEAR%", Integer.toString(movieRelease.getYear()));
        folder = folder.replace("%QUALITY%", movieRelease.getQuality());
        if (getLibrarySettings().isLibraryFolderReplaceSpace()) {
            folder = folder.replace(" ", getLibrarySettings().getLibraryFolderReplacingSpaceSign());
        }
        return Paths.get("", folder.split("%SEPARATOR%"));
    }

}
